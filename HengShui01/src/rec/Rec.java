package rec;
 import static org.bytedeco.javacpp.opencv_highgui.*;  
import static org.bytedeco.javacpp.opencv_imgcodecs.*;  
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.CvType;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;  
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import comm.Portsent;
import rec.ImgCut;
import rec.Util;

/**
 * @author LingLee
 *
 */
 //import static com.epiphan.vga2usb
//改
public class Rec {
	private static String path="D:/Param/samples/";
	private static String fileExctension=".jpg";
	private static int samplesNum=10;//模板个数0~9 和小数点
	private static String rectPath="D:/Param/rectData2.txt";
	private static String svm_save="D:/Param/SVM_DATA.xml";
	private static ArrayList<Rect> rects;//矩形框
	private static Size sampleSize;
	private static int recNum;//矩形框个数
	private static Mat[] samples;
	
	private static int gray1=150;
	private static int gray2=150;			
	private static int endCondition=1;
	private static int time=0;//输出时间间隔，跟cpu运行速度有关，粗略值
	private static int divide=12;//（标称值和实际值的分界线）
	private static String upResult="";//标称值输出string
	private static String lowResult="";
	public static BufferedWriter result;
	public static int[] up;//上部数字最值保存数组
	public static int[] low;
	public static int[] preUp;
	public static int[] preLow;
 	
	
	public static void readParam()throws Exception{//读入参数  标称值灰度 、实际值灰度、标称值个数，间隔时间、终止条件
		
		BufferedReader p=new BufferedReader(new FileReader("D:/Param/param.txt"));
		gray1=Integer.parseInt(p.readLine());
		gray2=Integer.parseInt(p.readLine());
		divide=Integer.parseInt(p.readLine());
		time=Integer.parseInt(p.readLine());
		endCondition=Integer.parseInt(p.readLine());
		p.close();
		System.out.println("gray1="+gray1+"\tgray2="+gray2+"\tdivide="+divide+"\ttime="
				+time+"endCondition="+endCondition);
		
	}
	
	public static SVM init()throws IOException{//初始化1.读入图片，svmtraining 2.读文件 初始化矩形框，各种参数
		up=new int[10];
		low=new int[10];
		preUp=new int[10];
		preLow=new int[10];
		//读入模板，初始化模板尺寸
		samples=new Mat[samplesNum];
		Util.readSamples(path, fileExctension, samplesNum, samples);
		sampleSize=samples[0].size();
		System.out.println("sample_size="+sampleSize.width()+" "+sampleSize.height());
		//读矩形框
		readRect();
		System.out.println(recNum);
		//SVM 训练模板
		SVM svm=new SVM();
		svm=Util.SVMTraining(samples,samplesNum,svm_save);
		
		//输出文档
		FileWriter file=new FileWriter("D:/Param/result.txt");
	    result=new BufferedWriter(file);
		return svm;
	}
	
	public static void readRect(){//读入矩形框位置  保存在rect中
		rects=new ArrayList<Rect>();
		int temp=0;
		try{
			BufferedReader in=new BufferedReader(new FileReader(rectPath));
			String line=null;
			int x,y,width,height;
			x=y=width=height=0;
			while((line=in.readLine())!=null){
				
				if(temp%4==0){
					x=Integer.parseInt(line);
				}
				else if(temp%4==1){
					y=Integer.parseInt(line);					
				}
				else if(temp%4==2){
					width=Integer.parseInt(line);					
				}
				else{
					height=Integer.parseInt(line);					
					//System.out.println(x+" "+y+" "+width+" "+height);
					rects.add(new Rect(x,y,width,height));
				}
				temp++;
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		recNum=rects.size();	
		System.out.println("recNum="+recNum);
	}
	
	public static void print()throws IOException{//输出函数，up标称值，low实际值
		//输出到文档		
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result.write(df.format(new Date()));
		result.newLine();
		result.write(upResult);
		result.newLine();
		
		result.write(lowResult);
		
		result.newLine();
		result.newLine();
		System.out.println(upResult);
		System.out.println(lowResult);
		//串口输出
		Portsent psent=new Portsent();
		psent.openSerialPort();
		psent.sentmsg(df.format(new Date())+"\n");
		psent.sentmsg(upResult+"\n");
		psent.sentmsg(lowResult+"\n");	
		//try{psent.wait(100);}catch(Exception e){e.printStackTrace();}
		waitKey(100);
		psent.closeSerialPort();
		
		result.flush();
		//result.close();
		upResult="";//输出后清空
		lowResult="";
		
	}
	public static Mat toMat(Image src) {//Image 转Mat形式

		BufferedImage bufImage=(BufferedImage)src;
	    ToMat iplConverter = new OpenCVFrameConverter.ToMat();
	    Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
	    Mat iplImage = iplConverter.convert(java2dConverter.convert(bufImage));
	    return iplImage;
	}
	
//	public static Mat toGray(Mat rgb){
//		Mat tempMat=new Mat();
//		Mat grayMat=new Mat();
//		cvtColor(rgb,tempMat,CV_RGB2GRAY);
//		for(int i=0;i<rgb.rows();i++){
//			for(int j=0;j<rgb.cols();j++){
//				BytePointer vec=rgb.ptr(i,j);
//				//System.out.println("bytePointer: "+vec.get(0)+" "+vec.get(1)+" "+vec.get(2));
//			}
//			
//		}
//		return grayMat;
//		
//	}
	
    public static void run(Image src, SVM svm)throws Exception{  //识别方法，传入参数：当前获取的视频帧，和svm
    	//init();
//    	CanvasFrame can=new CanvasFrame("hello");
//    	can.showImage(src);
//    	can.waitKey();
    	Mat srcImg=toMat(src);//iamge->Mat
//    	imshow("srcImg",srcImg);
//    	waitKey(10);
    	
    	int gray;
    	String preOut="";
		int timeTemp=0;
		int numCount=0;
    	//参数
		upResult="";
		lowResult="";
    	for(int i=0;i<recNum;i++){//对于每个矩形框，进行识别
			
			Mat imgRegion=new Mat(srcImg,rects.get(i));//获取框中内容
			
//			Mat togray=new Mat();
//	    	togray=toGray(srcImg);
//	    	imshow("toGray",togray);
//	    	waitKey();
//			
//			imshow("mat",imgRegion);
//			waitKey();
			System.out.println("gray1="+gray1+"gray2="+gray2);
			cvtColor(imgRegion, imgRegion, CV_RGB2GRAY);//rgb转换成灰度

			imshow("mat",imgRegion);
//			imwrite("D:/img.jpg",imgRegion);
			waitKey(10);		     
			ArrayList<Mat> upImg=new ArrayList<Mat>();
			ArrayList<Mat> lowImg=new ArrayList<Mat>();
			ImgCut imageCut=new ImgCut(imgRegion,sampleSize,gray1,gray2);//类ImgCut将数字组分割成单个数字，eg:567分成5，6，7
			int  upDigit=0;
			int lowDigit=0;
			try{
				imageCut.cut(imgRegion);
				 upDigit=imageCut.getUpDigit();//数字的位数，eg:567为3位数
				 lowDigit=imageCut.getLowDigit();
				 //System.out.println("digit="+upDigit+" "+lowDigit);
				upImg=imageCut.getUpImg();//单个数字数组
				lowImg=imageCut.getLowImg();
			}catch(Exception e){
				System.out.println("遮挡");break;
			}				
//			System.out.println("digit="+digit);			
			
			int upOut=Util.SVMRecognition(upImg, upDigit, svm_save,svm);//SVM识别结构，注意返回的是String形式
			int lowOut=Util.SVMRecognition(lowImg, lowDigit, svm_save,svm);
			//System.out.println("digit: "+upDigit+"\t"+lowDigit);
			
			//int upOutByTemp=Util.template(upImg, upDigit, samples);
			//int lowOutByTemp=Util.template(lowImg, lowDigit, samples);
			
			
			if(Math.abs(upOut-lowOut)>150&&lowOut>10){
				continue;
			}
			System.out.println("i="+i+"\toutputBySvm:  "+upOut+"\t"+lowOut);
			//System.out.println("outputByTemp: "+upOutByTemp+"\t"+lowOutByTemp);
			
			if(lowOut<10) numCount++;//统计当前帧中，实际值为0的个数，当所有实际值都为0时满足一个输出条件
			System.out.println("numCount="+numCount);
			if(numCount==recNum&&low[0]>0){//实际值全部为0，且第一个实际值的最值大于零，认为一个周期结束，可以输出
				for(int k=0;k<recNum;k++){
					if(up[k]==0) low[k]=0;
					if(up[k]>10&&low[k]<10) low[k]=up[k]-5;
					upResult+=" "+Integer.toString(up[k]);
					lowResult+=" "+Integer.toString(low[k]);
					up[k]=low[k]=0;
					preUp[k]=preLow[k]=0;
				}
				
				
				print();
				
				break;
			}
			if(preLow[i]==lowOut){
				if(lowOut>low[i]&&lowOut<9999) low[i]=lowOut;//更替最值
			}
			if(preUp[i]==upOut){
				if(upOut>up[i]&&upOut<9999)up[i]=upOut;
			}
			preLow[i]=lowOut;
			preUp[i]=upOut;
			
		}
		
		
		//已输出后，timeTemp,preOut等初始化
		
		//cvReleaseImage(grayImg);
	
//	result.close();
	//can.dispose();
	//cvDestroyAllWindows();
	//System.out.println("hello");   
    }  
}  
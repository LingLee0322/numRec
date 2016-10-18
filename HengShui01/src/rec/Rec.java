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
//��
public class Rec {
	private static String path="D:/Param/samples/";
	private static String fileExctension=".jpg";
	private static int samplesNum=10;//ģ�����0~9 ��С����
	private static String rectPath="D:/Param/rectData2.txt";
	private static String svm_save="D:/Param/SVM_DATA.xml";
	private static ArrayList<Rect> rects;//���ο�
	private static Size sampleSize;
	private static int recNum;//���ο����
	private static Mat[] samples;
	
	private static int gray1=150;
	private static int gray2=150;			
	private static int endCondition=1;
	private static int time=0;//���ʱ��������cpu�����ٶ��йأ�����ֵ
	private static int divide=12;//�����ֵ��ʵ��ֵ�ķֽ��ߣ�
	private static String upResult="";//���ֵ���string
	private static String lowResult="";
	public static BufferedWriter result;
	public static int[] up;//�ϲ�������ֵ��������
	public static int[] low;
	public static int[] preUp;
	public static int[] preLow;
 	
	
	public static void readParam()throws Exception{//�������  ���ֵ�Ҷ� ��ʵ��ֵ�Ҷȡ����ֵ���������ʱ�䡢��ֹ����
		
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
	
	public static SVM init()throws IOException{//��ʼ��1.����ͼƬ��svmtraining 2.���ļ� ��ʼ�����ο򣬸��ֲ���
		up=new int[10];
		low=new int[10];
		preUp=new int[10];
		preLow=new int[10];
		//����ģ�壬��ʼ��ģ��ߴ�
		samples=new Mat[samplesNum];
		Util.readSamples(path, fileExctension, samplesNum, samples);
		sampleSize=samples[0].size();
		System.out.println("sample_size="+sampleSize.width()+" "+sampleSize.height());
		//�����ο�
		readRect();
		System.out.println(recNum);
		//SVM ѵ��ģ��
		SVM svm=new SVM();
		svm=Util.SVMTraining(samples,samplesNum,svm_save);
		
		//����ĵ�
		FileWriter file=new FileWriter("D:/Param/result.txt");
	    result=new BufferedWriter(file);
		return svm;
	}
	
	public static void readRect(){//������ο�λ��  ������rect��
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
	
	public static void print()throws IOException{//���������up���ֵ��lowʵ��ֵ
		//������ĵ�		
		
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
		//�������
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
		upResult="";//��������
		lowResult="";
		
	}
	public static Mat toMat(Image src) {//Image תMat��ʽ

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
	
    public static void run(Image src, SVM svm)throws Exception{  //ʶ�𷽷��������������ǰ��ȡ����Ƶ֡����svm
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
    	//����
		upResult="";
		lowResult="";
    	for(int i=0;i<recNum;i++){//����ÿ�����ο򣬽���ʶ��
			
			Mat imgRegion=new Mat(srcImg,rects.get(i));//��ȡ��������
			
//			Mat togray=new Mat();
//	    	togray=toGray(srcImg);
//	    	imshow("toGray",togray);
//	    	waitKey();
//			
//			imshow("mat",imgRegion);
//			waitKey();
			System.out.println("gray1="+gray1+"gray2="+gray2);
			cvtColor(imgRegion, imgRegion, CV_RGB2GRAY);//rgbת���ɻҶ�

			imshow("mat",imgRegion);
//			imwrite("D:/img.jpg",imgRegion);
			waitKey(10);		     
			ArrayList<Mat> upImg=new ArrayList<Mat>();
			ArrayList<Mat> lowImg=new ArrayList<Mat>();
			ImgCut imageCut=new ImgCut(imgRegion,sampleSize,gray1,gray2);//��ImgCut��������ָ�ɵ������֣�eg:567�ֳ�5��6��7
			int  upDigit=0;
			int lowDigit=0;
			try{
				imageCut.cut(imgRegion);
				 upDigit=imageCut.getUpDigit();//���ֵ�λ����eg:567Ϊ3λ��
				 lowDigit=imageCut.getLowDigit();
				 //System.out.println("digit="+upDigit+" "+lowDigit);
				upImg=imageCut.getUpImg();//������������
				lowImg=imageCut.getLowImg();
			}catch(Exception e){
				System.out.println("�ڵ�");break;
			}				
//			System.out.println("digit="+digit);			
			
			int upOut=Util.SVMRecognition(upImg, upDigit, svm_save,svm);//SVMʶ��ṹ��ע�ⷵ�ص���String��ʽ
			int lowOut=Util.SVMRecognition(lowImg, lowDigit, svm_save,svm);
			//System.out.println("digit: "+upDigit+"\t"+lowDigit);
			
			//int upOutByTemp=Util.template(upImg, upDigit, samples);
			//int lowOutByTemp=Util.template(lowImg, lowDigit, samples);
			
			
			if(Math.abs(upOut-lowOut)>150&&lowOut>10){
				continue;
			}
			System.out.println("i="+i+"\toutputBySvm:  "+upOut+"\t"+lowOut);
			//System.out.println("outputByTemp: "+upOutByTemp+"\t"+lowOutByTemp);
			
			if(lowOut<10) numCount++;//ͳ�Ƶ�ǰ֡�У�ʵ��ֵΪ0�ĸ�����������ʵ��ֵ��Ϊ0ʱ����һ���������
			System.out.println("numCount="+numCount);
			if(numCount==recNum&&low[0]>0){//ʵ��ֵȫ��Ϊ0���ҵ�һ��ʵ��ֵ����ֵ�����㣬��Ϊһ�����ڽ������������
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
				if(lowOut>low[i]&&lowOut<9999) low[i]=lowOut;//������ֵ
			}
			if(preUp[i]==upOut){
				if(upOut>up[i]&&upOut<9999)up[i]=upOut;
			}
			preLow[i]=lowOut;
			preUp[i]=upOut;
			
		}
		
		
		//�������timeTemp,preOut�ȳ�ʼ��
		
		//cvReleaseImage(grayImg);
	
//	result.close();
	//can.dispose();
	//cvDestroyAllWindows();
	//System.out.println("hello");   
    }  
}  
package rec;
import static org.bytedeco.javacpp.opencv_highgui.*;  
import static org.bytedeco.javacpp.opencv_imgcodecs.*;  
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_ml.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.MatExpr;
import org.bytedeco.javacpp.opencv_objdetect.HOGDescriptor;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacpp.opencv_ml;
//import org.bytedeco.javacpp.opencv_ml.CvSVMParams;

public class Util {
	
	//读入模板
	public static void readSamples(final String pathName, final String fileExtension, final int num, Mat[] img){
		//char[] filename=new char[100];
		Mat srcImage;	
		for(int i=0;i<num;i++){
			String fileName=pathName+i+fileExtension;
			srcImage=imread(fileName);
//          IplImage srcIpl=cvLoadImage(fileName);
//			if(srcIpl.isNull()){
//				System.out.println("reading file error");
//				//exit(0)-------------
//			}
//			srcImage=new Mat(srcIpl);	
			cvtColor(srcImage,srcImage,COLOR_RGB2GRAY);
			threshold(srcImage,srcImage,150,255,1);
			img[i]=srcImage;
		}
		System.out.println("training ok!");
	}

	
	//SVM训练，返回已经训练好的svm
	public static int template(ArrayList<Mat> test, int maxNum,Mat[] samples){
		int result=0;
		Mat temp=new Mat();
		int oneNum=0;
		int simailar=0;
		
		for(int i=maxNum-1;i>=0;i--){
			Mat single=test.get(i);
			int max=0;
			for(int j=0;j<10;j++){
				absdiff(single, samples[j], temp);
				simailar=countNonZero(temp);
//				Size large=new Size(10,14);
//				Mat largeSample=new Mat();
//				resize(samples[j],largeSample,large);
//				imshow("largeSample",largeSample);
//				waitKey();
//				resize(temp,temp,large);
//				imshow("temp",temp);
//				waitKey();
				//System.out.print(simailar+"\t");
				if(simailar>max){
					
					max=simailar;
					oneNum=j;
				}
			}
			//System.out.println();
			result=result*10+oneNum;
			
		}
		//waitKey();
		return result;
	}
	public static SVM SVMTraining(Mat[] samples, int num,  String save){

		Mat trainData=new Mat();
		Mat trainLabel=new Mat(1,1764,CV_32SC1);
		
		int[] img_catg=new int[num];//-----------------------------动态数组?
		for(int i=0;i<num;i++){
			if(i<10)img_catg[i]=i;
			else if(i<20) img_catg[i]=i-10;
			else img_catg[i]=10;
		}
		Mat labelMat=new Mat(img_catg);
		transpose(labelMat,trainLabel);
		
		for(int i=0;i<num;i++){
			Mat src=samples[i];
			Mat trainImg=new Mat();
			resize(src,trainImg,new Size(32,32),0,0,INTER_CUBIC);
			HOGDescriptor hog=new HOGDescriptor(new Size(32,32), new Size(8,8), new Size(4,4), new Size(4,4), 9);
			
			FloatPointer descriptors=new FloatPointer();	
			hog.compute(trainImg, descriptors);
			try{hog.close();}catch(Exception e){e.printStackTrace();}
			Mat data_mat=new Mat(1,1764,CV_32FC1);
			Mat des_mat=new Mat(descriptors);
			transpose(des_mat,data_mat);
			trainData.push_back(data_mat);			
		}
		TrainData data=TrainData.create(trainData, ROW_SAMPLE, trainLabel);
		

		
		SVM svm=SVM.create();
//		ParamGrid params;
		TermCriteria criteria;
		criteria=new TermCriteria(CV_TERMCRIT_ITER, 100, 5e-3);
		
		
		//.create();
	//	params=new ParamGrid(SVM.C_SVC,SVM.RBF,12.0, 0.09, 1.0, 10.0, 0.5, 1.0, null,criteria);
		
		//svm相关参数
		
		svm.setType(SVM.C_SVC);
		svm.setKernel(SVM.RBF);//SVM.POLY
		svm.setDegree(12.0);
		svm.setGamma(0.09);
		svm.setCoef0(1.0);
		svm.setC(10.0);
		svm.setNu(0.5);
		svm.setP(1.0);
		//svm.setClassWeights(null);
		svm.setTermCriteria(criteria);
		
		svm.train(data);
		
		
		//TrainData tData=TrainData.create(arg0, arg1, arg2);
//		svm.train(trainData,ROW_SAMPLE,label);
////		svm.train(data_mat,0, res_mat);
////		svm.train(data_mat, 0, res_mat);
//		svm.save(save);
//		cvReleaseMat(data_mat);
//		cvReleaseMat(res_mat);
 
 
		svm.save(save);
		System.out.println("svm training ok!");
		return svm;
	}



//svm识别过程
	
	public static int SVMRecognition(ArrayList<Mat> test, int maxNum, final String save,SVM svm){
		int out=0;
//		SVM svm=SVM.create();
//		
//		svm.load(save);//save为svm.xml路径
//		svm=StatModel.load(save);//opencv3.0方法，但在Javacv1.2里不存在这个方法
//		StatModel s=new StatModel();

		
				
		Mat trainImg=new Mat();
		int temp;
		for(int i=maxNum-1;i>=0;i--){
			resize(test.get(i),trainImg,new Size(32,32),0,0,INTER_CUBIC);
						
			HOGDescriptor hog=new HOGDescriptor(new Size(32,32), new Size(8,8), new Size(4,4), new Size(4,4), 9);
			FloatPointer descriptors=new FloatPointer();
			hog.compute(trainImg, descriptors);
			
			Mat svmTemp=new Mat(descriptors);
			try{hog.close();}catch(Exception e){e.printStackTrace();}
			Mat SVMtrainMat=new Mat(1, 1764, CV_32FC1);
			
			transpose(svmTemp,SVMtrainMat);
			
            
			temp=(int)svm.predict(SVMtrainMat);
			//System.out.println("maxNum="+maxNum+"temp="+temp);
			//System.out.print("temp="+temp);
			if(temp<10) out=out*10+temp;
			else out=out*10+(temp-10);
			
//			char c;
//			switch(temp){
//			case 0: c='0'; break;
//			case 1: c='1';break;
//			case 2: c='2'; break;
//			case 3: c='3';break;
//			case 4: c='4';break;
//			case 5: c='5';break;
//			case 6: c='6';break;
//			case 7: c='7';break;
//			case 8: c='8';break;
//			case 9: c='9';break;
//			case 10: c='.';break;
//			default: c=' ';
//			}
			
		}	
		return out;//返回识别结果
	}
}

	
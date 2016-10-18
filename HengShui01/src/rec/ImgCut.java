package rec;

import java.util.ArrayList;

import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.javacpp.opencv_highgui.*;  
import static org.bytedeco.javacpp.opencv_imgcodecs.*;  
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class ImgCut {
	private static int upDigit;//标称值的位数
	private static int lowDigit;
	private static int upGray;//标称值的灰度
	private static int downGray;
	private ArrayList<Mat> upImg;//标称值分割后的数组
	private ArrayList<Mat> lowImg;
	private static Size sampleSize;
	Mat one=imread("D:/Param/samples/1.jpg");//图片1.jpg
	
	public ImgCut(Mat srcImg, Size sampleSize, int upGray,int downGray){//构造方法
		this.upGray=upGray;
		this.downGray=downGray;
		this.sampleSize=sampleSize;
		upDigit=lowDigit=0;
		upImg=new ArrayList<Mat>();
		lowImg=new ArrayList<Mat>();
	}
	
	public int getUpDigit(){
		return upDigit;
	}
	public int getLowDigit(){
		return lowDigit;
	}
	public ArrayList<Mat> getLowImg(){
		return lowImg;
	}
	public ArrayList<Mat> getUpImg(){
		return upImg;
	}
	
	//切割单个数字
	public int  singleCut(Mat srcImg,int gray,int[] colHeight,int height,ArrayList<Mat> outputImg){
		int digit=0;
		Rect tempRect;
		Mat tempMat;
		int tempX=0;
		int tempHeight=0;//数字最大高度
			
		threshold(srcImg,srcImg,gray,255,1);
		
		for(int j=colHeight.length-1;j>=0;j--){//纵向，从右至左开始统计
			if(colHeight[j]<1){continue;}//小于1，说明此列白点数为0，即该列为北京
			if(tempHeight<colHeight[j]) tempHeight=colHeight[j];
			if(colHeight[j+1]<1){//tempX记录 数字的右侧边缘部分
				tempX=j+1;
			}
			if(colHeight[j-1]==0){//数字的左侧边缘部分，则从j到tempx为数字部分
				//tempHeight=0;
				if (tempX - j > height&&tempX - j < 2 * height)//tempx-j为该数字的宽，根据数字比例，当宽度大于高度height且小于两个高度的时候，出现两个数字粘连
				{
					tempRect = new Rect(tempX - (tempX - j) / 2, 0, (tempX - j) / 2, height);
					tempMat= new Mat(srcImg, tempRect);
					resize(tempMat, tempMat, sampleSize);//尺寸归一化
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					
					outputImg.add(tempMat);//归一化后，数字添加到ArrayList中
				
					tempRect = new Rect(j, 0, (tempX - j) / 2, height);
					tempMat = new Mat(srcImg,tempRect);
					resize(tempMat, tempMat, sampleSize);//尺寸归一化
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
					digit = digit + 2;
				}
				else if(tempX - j >=2 * height){//三个数字粘连
					int width=(tempX - j) / 3;
					tempRect = new Rect(tempX - width, 0, width, height);
					tempMat= new Mat(srcImg, tempRect);
					resize(tempMat, tempMat, sampleSize);//尺寸归一化
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
				
					tempRect = new Rect(tempX -2* width, 0, width, height);
					tempMat= new Mat(srcImg, tempRect);
					resize(tempMat, tempMat, sampleSize);//尺寸归一化
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
					
					tempRect = new Rect(j, 0, width, height);
					tempMat = new Mat(srcImg,tempRect);
					resize(tempMat, tempMat, sampleSize);//尺寸归一化
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
					digit = digit + 3;
				}
				else{//单个数字
					if(tempX-j>0){
						tempRect = new Rect(j, 0, (tempX - j), height);
						tempMat = new Mat(srcImg,tempRect);
						
//						if(tempMat.size().width()<=(height)/2&&tempMat.size().width()>=2){
//							//根据数字的宽度，判断数字是否为1，若为1，直接用模板替代
//							cvtColor(one,tempMat,CV_RGB2GRAY);
//							tempHeight=0;
//						}
//						imshow("imgRegion1",tempMat);
//						waitKey();
						
						//	imwrite("D:/Project/Samples2/s1/a.jpg",tempUpMat);
						resize(tempMat, tempMat, sampleSize);//尺寸归一化
						threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
						outputImg.add(tempMat);
						digit++;
					}
				}
			}		
		}
		return digit;//返回数字的位数
	}
	public boolean cut(Mat imgRegion){//分割方法
		
	//	Size imgRegionSize=imgRegion.size();
		int[] colHeight=new int[imgRegion.cols()];//图片，纵向白点个数，图片Mat为一个矩阵，此处为各列的白点的个数。白色为数字部分，黑色为背景部分
		int []colLowHeight=new int[imgRegion.cols()];//下半数字（实际值），纵向白点个数
		int[] rowHeight=new int[imgRegion.rows()];//图片，横向白点个数
		int value;
		//cvtColor(imgRegion, imgRegion, CV_RGB2GRAY);
		threshold(imgRegion,imgRegion,upGray,255,0);//统一二值化，此处以标称值的灰度为准，为统计白点，分割开上下数字
		//System.out.println("width="+imgRegion.cols()+"\t"+imgRegion.rows());
		for(int i=0; i<imgRegion.cols();i++){//统计Mat的白点个数
			for(int j=0;j<imgRegion.rows();j++){
				BytePointer p=imgRegion.ptr(j, i);
				value=p.get();
				
				//System.out.print(" "+value);
				if(value==0){//value=0代表该位置的灰度值为255，是白色的
					colHeight[i]++;
					rowHeight[j]++;
				}	
				
			}
			//System.out.print(colHeight[i]+"\t");
		}
		
		
		int minY, maxY,tempX,tempY;
		int height=0;
		maxY=0;minY=imgRegion.rows()-1;
		tempY=0;
		//System.out.println(imgRegion.rows()+" "+imgRegion.cols());
		for(int i=imgRegion.rows()-1;i>=0;i--){
			//System.out.print(rowHeight[i]+"\t");
			if(rowHeight[i]>1){
				if(minY>=i) minY=i;//（x,y,width,height）四元组表示数字的准确位置，minY为上部数字的y值
				if(maxY<i) maxY=i;//下部数字的（y+height）值
				if(rowHeight[i+1]==0){
					tempY=i+1;
				}
				if(rowHeight[i-1]==0){
					height=tempY-i;//数字的高度
				}
			}
		}
	//	System.out.println("height="+height);
		if(height<=0) return false;//数字高度《=0时，表示没有数字 返回false
		
	
		
		//上下部分数字分隔开，并各自二值化
		Rect upRect=new Rect(0,minY,imgRegion.size().width(),height);//上边数字的矩形框
		Rect lowRect=new Rect(0,maxY-height+1,imgRegion.size().width(),height);
		Mat upRegion=new Mat(imgRegion,upRect);//上部数字图片
		Mat lowRegion=new Mat(imgRegion,lowRect);//下部数字
		
		threshold(upRegion,upRegion,upGray,255,0);//按照各自灰度二值化
		threshold(lowRegion,lowRegion,downGray,255,0);
		imshow("upRegion",upRegion);//显示
		waitKey(10);
		imshow("lowRegion",lowRegion);
		waitKey(10);
		
		for(int i=0;i<imgRegion.cols();i++){//在纵向上将两个数字的白点值分隔开，便于各自在分隔
			for(int j=0;j<height;j++){
				BytePointer p=lowRegion.ptr(j, i);
				value=p.get();
				
				//System.out.print("   value="+value);
				if(value==0){
					colLowHeight[i]++;//下部数字的纵向白点个数
				}
			}
			colHeight[i]-=colLowHeight[i];//上部数字的纵向白点个数
			//System.out.print(colHeight[i]+"\t");
			//System.out.println();
			//System.out.print(colLowHeight[i]+"\t");
		}
		
		upDigit=singleCut(upRegion,upGray,colHeight,height,upImg);//数字切割成单个数字组成的数组
		lowDigit=singleCut(lowRegion,downGray,colLowHeight,height,lowImg);
		
		return true;
		
	}
	
	
}

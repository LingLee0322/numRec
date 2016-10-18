package rec;

import java.util.ArrayList;

import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.javacpp.opencv_highgui.*;  
import static org.bytedeco.javacpp.opencv_imgcodecs.*;  
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class ImgCut {
	private static int upDigit;//���ֵ��λ��
	private static int lowDigit;
	private static int upGray;//���ֵ�ĻҶ�
	private static int downGray;
	private ArrayList<Mat> upImg;//���ֵ�ָ�������
	private ArrayList<Mat> lowImg;
	private static Size sampleSize;
	Mat one=imread("D:/Param/samples/1.jpg");//ͼƬ1.jpg
	
	public ImgCut(Mat srcImg, Size sampleSize, int upGray,int downGray){//���췽��
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
	
	//�и������
	public int  singleCut(Mat srcImg,int gray,int[] colHeight,int height,ArrayList<Mat> outputImg){
		int digit=0;
		Rect tempRect;
		Mat tempMat;
		int tempX=0;
		int tempHeight=0;//�������߶�
			
		threshold(srcImg,srcImg,gray,255,1);
		
		for(int j=colHeight.length-1;j>=0;j--){//���򣬴�������ʼͳ��
			if(colHeight[j]<1){continue;}//С��1��˵�����а׵���Ϊ0��������Ϊ����
			if(tempHeight<colHeight[j]) tempHeight=colHeight[j];
			if(colHeight[j+1]<1){//tempX��¼ ���ֵ��Ҳ��Ե����
				tempX=j+1;
			}
			if(colHeight[j-1]==0){//���ֵ�����Ե���֣����j��tempxΪ���ֲ���
				//tempHeight=0;
				if (tempX - j > height&&tempX - j < 2 * height)//tempx-jΪ�����ֵĿ��������ֱ���������ȴ��ڸ߶�height��С�������߶ȵ�ʱ�򣬳�����������ճ��
				{
					tempRect = new Rect(tempX - (tempX - j) / 2, 0, (tempX - j) / 2, height);
					tempMat= new Mat(srcImg, tempRect);
					resize(tempMat, tempMat, sampleSize);//�ߴ��һ��
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					
					outputImg.add(tempMat);//��һ����������ӵ�ArrayList��
				
					tempRect = new Rect(j, 0, (tempX - j) / 2, height);
					tempMat = new Mat(srcImg,tempRect);
					resize(tempMat, tempMat, sampleSize);//�ߴ��һ��
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
					digit = digit + 2;
				}
				else if(tempX - j >=2 * height){//��������ճ��
					int width=(tempX - j) / 3;
					tempRect = new Rect(tempX - width, 0, width, height);
					tempMat= new Mat(srcImg, tempRect);
					resize(tempMat, tempMat, sampleSize);//�ߴ��һ��
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
				
					tempRect = new Rect(tempX -2* width, 0, width, height);
					tempMat= new Mat(srcImg, tempRect);
					resize(tempMat, tempMat, sampleSize);//�ߴ��һ��
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
					
					tempRect = new Rect(j, 0, width, height);
					tempMat = new Mat(srcImg,tempRect);
					resize(tempMat, tempMat, sampleSize);//�ߴ��һ��
					threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
					outputImg.add(tempMat);
					digit = digit + 3;
				}
				else{//��������
					if(tempX-j>0){
						tempRect = new Rect(j, 0, (tempX - j), height);
						tempMat = new Mat(srcImg,tempRect);
						
//						if(tempMat.size().width()<=(height)/2&&tempMat.size().width()>=2){
//							//�������ֵĿ�ȣ��ж������Ƿ�Ϊ1����Ϊ1��ֱ����ģ�����
//							cvtColor(one,tempMat,CV_RGB2GRAY);
//							tempHeight=0;
//						}
//						imshow("imgRegion1",tempMat);
//						waitKey();
						
						//	imwrite("D:/Project/Samples2/s1/a.jpg",tempUpMat);
						resize(tempMat, tempMat, sampleSize);//�ߴ��һ��
						threshold(tempMat, tempMat, gray, 255, 0);//CV_THRESH_BINARY_INV
						outputImg.add(tempMat);
						digit++;
					}
				}
			}		
		}
		return digit;//�������ֵ�λ��
	}
	public boolean cut(Mat imgRegion){//�ָ��
		
	//	Size imgRegionSize=imgRegion.size();
		int[] colHeight=new int[imgRegion.cols()];//ͼƬ������׵������ͼƬMatΪһ�����󣬴˴�Ϊ���еİ׵�ĸ�������ɫΪ���ֲ��֣���ɫΪ��������
		int []colLowHeight=new int[imgRegion.cols()];//�°����֣�ʵ��ֵ��������׵����
		int[] rowHeight=new int[imgRegion.rows()];//ͼƬ������׵����
		int value;
		//cvtColor(imgRegion, imgRegion, CV_RGB2GRAY);
		threshold(imgRegion,imgRegion,upGray,255,0);//ͳһ��ֵ�����˴��Ա��ֵ�ĻҶ�Ϊ׼��Ϊͳ�ư׵㣬�ָ��������
		//System.out.println("width="+imgRegion.cols()+"\t"+imgRegion.rows());
		for(int i=0; i<imgRegion.cols();i++){//ͳ��Mat�İ׵����
			for(int j=0;j<imgRegion.rows();j++){
				BytePointer p=imgRegion.ptr(j, i);
				value=p.get();
				
				//System.out.print(" "+value);
				if(value==0){//value=0�����λ�õĻҶ�ֵΪ255���ǰ�ɫ��
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
				if(minY>=i) minY=i;//��x,y,width,height����Ԫ���ʾ���ֵ�׼ȷλ�ã�minYΪ�ϲ����ֵ�yֵ
				if(maxY<i) maxY=i;//�²����ֵģ�y+height��ֵ
				if(rowHeight[i+1]==0){
					tempY=i+1;
				}
				if(rowHeight[i-1]==0){
					height=tempY-i;//���ֵĸ߶�
				}
			}
		}
	//	System.out.println("height="+height);
		if(height<=0) return false;//���ָ߶ȡ�=0ʱ����ʾû������ ����false
		
	
		
		//���²������ַָ����������Զ�ֵ��
		Rect upRect=new Rect(0,minY,imgRegion.size().width(),height);//�ϱ����ֵľ��ο�
		Rect lowRect=new Rect(0,maxY-height+1,imgRegion.size().width(),height);
		Mat upRegion=new Mat(imgRegion,upRect);//�ϲ�����ͼƬ
		Mat lowRegion=new Mat(imgRegion,lowRect);//�²�����
		
		threshold(upRegion,upRegion,upGray,255,0);//���ո��ԻҶȶ�ֵ��
		threshold(lowRegion,lowRegion,downGray,255,0);
		imshow("upRegion",upRegion);//��ʾ
		waitKey(10);
		imshow("lowRegion",lowRegion);
		waitKey(10);
		
		for(int i=0;i<imgRegion.cols();i++){//�������Ͻ��������ֵİ׵�ֵ�ָ��������ڸ����ڷָ�
			for(int j=0;j<height;j++){
				BytePointer p=lowRegion.ptr(j, i);
				value=p.get();
				
				//System.out.print("   value="+value);
				if(value==0){
					colLowHeight[i]++;//�²����ֵ�����׵����
				}
			}
			colHeight[i]-=colLowHeight[i];//�ϲ����ֵ�����׵����
			//System.out.print(colHeight[i]+"\t");
			//System.out.println();
			//System.out.print(colLowHeight[i]+"\t");
		}
		
		upDigit=singleCut(upRegion,upGray,colHeight,height,upImg);//�����и�ɵ���������ɵ�����
		lowDigit=singleCut(lowRegion,downGray,colLowHeight,height,lowImg);
		
		return true;
		
	}
	
	
}

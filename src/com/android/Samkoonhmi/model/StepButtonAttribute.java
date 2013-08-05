package com.android.Samkoonhmi.model;

import android.graphics.Color;

import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

public class StepButtonAttribute {
	private int currentTextColor=Color.BLACK;//��ǰ�ı�����ɫ
	private int notCurrentTextColor=Color.BLACK;//���ǵ�ǰ���ı�����ɫ
	private int currentBackColor=Color.WHITE;//��ǰ�ı�����ɫ
	private int notCurrentBackColor=Color.GRAY;//���ǵ�ǰ�ı�����ɫ
	private int divideLineColor=Color.BLACK;//�ָ��ߵ���ɫ
	private String textContent;//���ֵ�����
	private int fontSize=30;//����Ĵ�С
	private String fontFamily="����";//�������ʽ
	private int  fontSpace=0;//�ּ��
	private TEXT_PIC_ALIGN textAlign=TEXT_PIC_ALIGN.CENTER;//�ֵ����λ��
	private int imageSource;//ͼƬ����Դ 1 :ϵͳ 2���ļ�
	private String imagePaht;//ͼƬ��·��
	private boolean isSuitShape;//�Ƿ���Ͽؼ��Ĵ�С
	private int marginSpace;//�߾�
	private TEXT_PIC_ALIGN imageAlign;//ͼƬ�����λ��
	
	public StepButtonAttribute() {
		super();
	}
	public StepButtonAttribute(int currentTextColor, int notCurrentTextColor,
			int currentBackColor, int notCurrentBackColor, int divideLineColor,
			String textContent, int fontSize, String fontFamily, int fontSpace,
			TEXT_PIC_ALIGN textAlign, int imageSource, String imagePaht,
			boolean isSuitShape, int marginSpace, TEXT_PIC_ALIGN imageAlign) {
		super();
		this.currentTextColor = currentTextColor;
		this.notCurrentTextColor = notCurrentTextColor;
		this.currentBackColor = currentBackColor;
		this.notCurrentBackColor = notCurrentBackColor;
		this.divideLineColor = divideLineColor;
		this.textContent = textContent;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
		this.fontSpace = fontSpace;
		this.textAlign = textAlign;
		this.imageSource = imageSource;
		this.imagePaht = imagePaht;
		this.isSuitShape = isSuitShape;
		this.marginSpace = marginSpace;
		this.imageAlign = imageAlign;
	}
	public int getCurrentTextColor() {
		return currentTextColor;
	}
	public void setCurrentTextColor(int currentTextColor) {
		this.currentTextColor = currentTextColor;
	}
	public int getNotCurrentTextColor() {
		return notCurrentTextColor;
	}
	public void setNotCurrentTextColor(int notCurrentTextColor) {
		this.notCurrentTextColor = notCurrentTextColor;
	}
	public int getCurrentBackColor() {
		return currentBackColor;
	}
	public void setCurrentBackColor(int currentBackColor) {
		this.currentBackColor = currentBackColor;
	}
	public int getNotCurrentBackColor() {
		return notCurrentBackColor;
	}
	public void setNotCurrentBackColor(int notCurrentBackColor) {
		this.notCurrentBackColor = notCurrentBackColor;
	}
	public int getDivideLineColor() {
		return divideLineColor;
	}
	public void setDivideLineColor(int divideLineColor) {
		this.divideLineColor = divideLineColor;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public String getFontFamily() {
		return fontFamily;
	}
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}
	public int getFontSpace() {
		return fontSpace;
	}
	public void setFontSpace(int fontSpace) {
		this.fontSpace = fontSpace;
	}
	public TEXT_PIC_ALIGN getTextAlign() {
		return textAlign;
	}
	public void setTextAlign(TEXT_PIC_ALIGN textAlign) {
		this.textAlign = textAlign;
	}
	public int getImageSource() {
		return imageSource;
	}
	public void setImageSource(int imageSource) {
		this.imageSource = imageSource;
	}
	public String getImagePaht() {
		return imagePaht;
	}
	public void setImagePaht(String imagePaht) {
		this.imagePaht = imagePaht;
	}
	public boolean isSuitShape() {
		return isSuitShape;
	}
	public void setSuitShape(boolean isSuitShape) {
		this.isSuitShape = isSuitShape;
	}
	public int getMarginSpace() {
		return marginSpace;
	}
	public void setMarginSpace(int marginSpace) {
		this.marginSpace = marginSpace;
	}
	public TEXT_PIC_ALIGN getImageAlign() {
		return imageAlign;
	}
	public void setImageAlign(TEXT_PIC_ALIGN imageAlign) {
		this.imageAlign = imageAlign;
	}
	
	

}

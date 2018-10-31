package com.keda.amap.traffic.model.kafka;

import java.util.Date;

/**
 * Created by lcy on 2018/4/18.
 */
public class Message {
	private String XXBH;
	private String XXLB;
	private String XXDX;
	private String XXNR;
	private String XXLY;
	private String ZDYSJ;
	private String CZLX;
	private KafkaOperaUser CZYH;
	private KafkaOperaOrg CZDW;
	private Date CZSJ;

	public String getXXBH() {
		return XXBH;
	}

	public void setXXBH(String XXBH) {
		this.XXBH = XXBH;
	}

	public String getXXLB() {
		return XXLB;
	}

	public void setXXLB(String XXLB) {
		this.XXLB = XXLB;
	}

	public String getXXDX() {
		return XXDX;
	}

	public void setXXDX(String XXDX) {
		this.XXDX = XXDX;
	}

	public String getXXNR() {
		return XXNR;
	}

	public void setXXNR(String XXNR) {
		this.XXNR = XXNR;
	}

	public String getXXLY() {
		return XXLY;
	}

	public void setXXLY(String XXLY) {
		this.XXLY = XXLY;
	}

	public String getZDYSJ() {
		return ZDYSJ;
	}

	public void setZDYSJ(String ZDYSJ) {
		this.ZDYSJ = ZDYSJ;
	}

	public String getCZLX() {
		return CZLX;
	}

	public void setCZLX(String CZLX) {
		this.CZLX = CZLX;
	}

	public KafkaOperaUser getCZYH() {
		return CZYH;
	}

	public void setCZYH(KafkaOperaUser CZYH) {
		this.CZYH = CZYH;
	}

	public KafkaOperaOrg getCZDW() {
		return CZDW;
	}

	public void setCZDW(KafkaOperaOrg CZDW) {
		this.CZDW = CZDW;
	}

	public Date getCZSJ() {
		return CZSJ;
	}

	public void setCZSJ(Date CZSJ) {
		this.CZSJ = CZSJ;
	}
}

package com.keda.amap.traffic.model.kafka;

/** kafka操作单位
 * Created by lcy on 2018/4/18.
 */
public class KafkaOperaOrg {
	private String DWBH;
	private String DWMC;
	private String DWJC;
	private String DWNBBM;

	public String getDWBH() {
		return DWBH;
	}

	public void setDWBH(String DWBH) {
		this.DWBH = DWBH;
	}

	public String getDWMC() {
		return DWMC;
	}

	public void setDWMC(String DWMC) {
		this.DWMC = DWMC;
	}

	public String getDWJC() {
		return DWJC;
	}

	public void setDWJC(String DWJC) {
		this.DWJC = DWJC;
	}

	public String getDWNBBM() {
		return DWNBBM;
	}

	public void setDWNBBM(String DWNBBM) {
		this.DWNBBM = DWNBBM;
	}
}

package com.tuling.setInject;

import org.springframework.stereotype.Component;

/**
 * Created by smlz on 2019/8/22.
 */
@Component
public class InstA {

	//@Autowired
	private InstB instB;

	public InstB getInstB() {
		return instB;
	}

	public void setInstB(InstB instB) {
		System.out.println("InstA 的注入方式......");
		this.instB = instB;
	}

	public InstA() {
		System.out.println("InstA的构造方法.....");
	}

	@Override
	public String toString() {
		return "InstA{" +
				"instB=" + instB +
				'}';
	}
}

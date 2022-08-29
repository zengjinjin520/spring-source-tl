/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * Inner class that implements a Pointcut that matches if the underlying
 * {@link TransactionAttributeSource} has an attribute for a given method.
 *
 * @author Juergen Hoeller
 * @since 2.5.5
 * 首先他的访问权限是default 显示是给内部使用的
 * 首先它继承自StaticMethodMatcherPointcut 所以`ClassFilter classFilter = ClassFilter.True;` 匹配所有的类
 * 并且isRuntime = false 表示值需要对方法进行静态匹配即可~~~
 */
@SuppressWarnings("serial")
abstract class TransactionAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

	/**
	 *
	 * @param method the candidate method
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the candidate class must be taken to be the method's declaring class)
	 * 方法的匹配 静态匹配即可（因为事务无需动态匹配这个细粒度~~~）
	 * @return
	 */
	@Override
	public boolean matches(Method method, @Nullable Class<?> targetClass) {
		// TransactionalProxy它是SpringProxy的子类。如果是被TransactionProxyFactoryBean生产出来的Bean，就会自动实现此接口，那么就不会被这里再次代理了
		if (targetClass != null && TransactionalProxy.class.isAssignableFrom(targetClass)) {
			return false;
		}
		/**
		 * 获取我们@EnableTransactionManagement注解为我们容器中导入的ProxyTransactionManagementConfiguration
         * 配置类中的TransactionAttributeSource对象
		 *
		 * 重要：拿到事务属性源~~~~~~
		 * 如果tas == null 表示没有配置事务属性源，那是全部匹配的 也就是说所有的方法都匹配~~~（这个处理还是比较让我诧异的~~~）
		 * 或者 标注了@transaction这样注解的方法才会给与之匹配~~~
		 */
		TransactionAttributeSource tas = getTransactionAttributeSource();
		//若事务属性||解析出来的事务注解属性不为空表示方法匹配
		return (tas == null || tas.getTransactionAttribute(method, targetClass) != null);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TransactionAttributeSourcePointcut)) {
			return false;
		}
		TransactionAttributeSourcePointcut otherPc = (TransactionAttributeSourcePointcut) other;
		return ObjectUtils.nullSafeEquals(getTransactionAttributeSource(), otherPc.getTransactionAttributeSource());
	}

	@Override
	public int hashCode() {
		return TransactionAttributeSourcePointcut.class.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getTransactionAttributeSource();
	}


	/**
	 * Obtain the underlying TransactionAttributeSource (may be {@code null}).
	 * To be implemented by subclasses.
	 * 由子类提供给我，告诉事务属性源~~~ 我才好知道哪些方法需要切嘛~~~
	 */
	@Nullable
	protected abstract TransactionAttributeSource getTransactionAttributeSource();

}

/**
 * 
 */
package com.fxsession.fastplus.fpf;


/**
 * @author Dmitry Vulf
 *
 */
public interface IFPFHandler {
	public String getSiteID();
	public void push(FPFMessage message); 
}

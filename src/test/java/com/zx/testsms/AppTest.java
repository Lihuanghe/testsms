package com.zx.testsms;

import java.util.Iterator;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;
import com.zx.sms.connect.manager.smpp.SMPPClientEndpointEntity;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
        SMPPClientEndpointEntity smpp = new SMPPClientEndpointEntity();
        CMPPClientEndpointEntity cmpp = new CMPPClientEndpointEntity();
       
       
        printBeanproperties(smpp);
        printBeanproperties(cmpp);
       
    }
    
    private void printBeanproperties(Object bean) {
        BeanMap beanMap = new BeanMap(bean);
       for(Iterator<String> keyitor =  beanMap.keyIterator() ;keyitor.hasNext();) {
    	   String key = keyitor.next();
    	   logger.info("{} : {}" ,bean.getClass().getSimpleName(),key);
       }
       logger.info("============");
    }
}

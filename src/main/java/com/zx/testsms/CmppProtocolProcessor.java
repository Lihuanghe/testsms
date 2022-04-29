package com.zx.testsms;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsDcs;
import org.marre.sms.SmsTextMessage;

import com.zx.sms.BaseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;

public class CmppProtocolProcessor implements ProtocolProcessor {

	public BaseMessage buildMsg(String telephone, String txt, CommandLine line,Map<String, String> queryMap) {
		return buildCmppMsg(telephone,txt,line,queryMap);
	}
	
	public  EndpointEntity  buildClient(Map<String,String> queryMap) {
    	CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
        String userName = queryMap.get("username");
        String pass = queryMap.get("password");
        String version = queryMap.get("version");
        String spcode = queryMap.get("spcode");
        String msgsrc = queryMap.get("msgsrc");
        String serviceid = queryMap.get("serviceid");
        
        client.setPassword(pass);
        client.setUserName(userName);
        client.setVersion(Integer.valueOf(version).shortValue());
        client.setSpCode(spcode);
        client.setMsgSrc(msgsrc);
        client.setServiceId(serviceid);
        return client;
 }
	
	  private  BaseMessage buildCmppMsg(String telephone,String txt ,CommandLine line,Map<String, String> queryMap) {
	    	
	        //new 一个短信 Request对象
	        CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
	        msg.setDestterminalId(telephone);
	        msg.setLinkID("0000");
	        
	        msg.setRegisteredDelivery((short) 1);
	        String content = "test-sms" ;
	        if(StringUtils.isNotBlank(txt)) {
	        	content = txt;
	        }
	        String dcs = line.getOptionValue("dcs");
	        if(StringUtils.isBlank(dcs)) {
	        	msg.setMsgContent(content);
	       }else {
	    	    msg.setMsg(new SmsTextMessage(content,new SmsDcs(Byte.valueOf(dcs))));
	       }
	        
	        String attime = line.getOptionValue("attime");
	        if(StringUtils.isNotBlank(attime)) {
	        	msg.setAtTime(attime);
	       }
	        
	        String spcode = line.getOptionValue("spcode");
	        if(StringUtils.isNotBlank(spcode)) {
	        	msg.setSrcId(spcode);
	       }
	        String msgsrc = line.getOptionValue("msgsrc");
	        if(StringUtils.isNotBlank(msgsrc)) {
	        	msg.setMsgsrc(msgsrc);
	       }
	        return msg;
	    }

}

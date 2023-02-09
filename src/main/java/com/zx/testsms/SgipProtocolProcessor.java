package com.zx.testsms;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;

import com.chinamobile.cmos.sms.SmsDcs;
import com.chinamobile.cmos.sms.SmsMessage;
import com.chinamobile.cmos.sms.SmsTextMessage;
import com.chinamobile.cmos.wap.push.SmsWapPushMessage;
import com.chinamobile.cmos.wap.push.WapSLPush;
import com.zx.sms.BaseMessage;
import com.zx.sms.codec.sgip12.msg.SgipSubmitRequestMessage;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.sgip.SgipClientEndpointEntity;

public class SgipProtocolProcessor implements ProtocolProcessor {

	public BaseMessage buildMsg(String telephone, String txt, CommandLine line, Map<String, String> queryMap) {
		SgipSubmitRequestMessage requestMessage = new SgipSubmitRequestMessage();

		requestMessage.setReportflag((short) 1);
		String content = "test-sms";
		if (StringUtils.isNotBlank(txt)) {
			content = txt;
		}
		String dcs = line.getOptionValue("dcs");
		if (StringUtils.isBlank(dcs)) {
			requestMessage.setMsgContent(content);
		} else {
			requestMessage.setMsgContent(new SmsTextMessage(content, new SmsDcs(Byte.valueOf(dcs))));
		}
		
		if(line.hasOption("wap")) {
			WapSLPush sl = new WapSLPush(content);
			SmsMessage wap = new SmsWapPushMessage(sl);
			requestMessage.setMsgContent(wap);
		}
		
		String spcode = line.getOptionValue("spcode");
		if (StringUtils.isNotBlank(spcode)) {
			requestMessage.setSpnumber(spcode);
		}else {
			requestMessage.setSpnumber(queryMap.get("spcode"));
		}

		String attime = line.getOptionValue("attime");
		if (StringUtils.isNotBlank(attime)) {
			requestMessage.setScheduletime(attime);
		}

		String msgsrc = line.getOptionValue("msgsrc");
		if (StringUtils.isNotBlank(msgsrc)) {
			requestMessage.setCorpid(msgsrc);
		} else {
			requestMessage.setCorpid(queryMap.get("corpid"));
		}

		String servicetype = queryMap.get("servicetype");
		if (StringUtils.isNotBlank(servicetype)) {
			requestMessage.setServicetype(servicetype);
		}
		requestMessage.setUsernumber(telephone);
		return requestMessage;
	}

	public EndpointEntity buildClient(Map<String, String> queryMap) {
		SgipClientEndpointEntity sgip = new SgipClientEndpointEntity();
		String userName = queryMap.get("username");
		String pass = queryMap.get("password");
		String nodeid = queryMap.get("nodeid");
		sgip.setLoginName(userName);
		sgip.setLoginPassowrd(pass);
		sgip.setNodeId(Long.valueOf(nodeid));
		return sgip;
	}

}

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
import com.zx.sms.codec.smgp.msg.SMGPSubmitMessage;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.smgp.SMGPClientEndpointEntity;

public class SMGPProtocolProcessor implements ProtocolProcessor {

	public BaseMessage buildMsg(String telephone, String txt, CommandLine line, Map<String, String> queryMap) {
		SMGPSubmitMessage pdu = new SMGPSubmitMessage();
		pdu.setNeedReport(true);

		pdu.setDestTermIdArray(telephone);

		String content = "test-sms";
		if (StringUtils.isNotBlank(txt)) {
			content = txt;
		}
		String dcs = line.getOptionValue("dcs");
		if (StringUtils.isBlank(dcs)) {
			pdu.setMsgContent(content);
		} else {
			pdu.setMsgContent(new SmsTextMessage(content, new SmsDcs(Byte.valueOf(dcs))));
		}
		
		if(line.hasOption("wap")) {
			WapSLPush sl = new WapSLPush(content);
			SmsMessage wap = new SmsWapPushMessage(sl);
			pdu.setMsgContent(wap);
		}
		
		String spcode = line.getOptionValue("spcode");
		if (StringUtils.isNotBlank(spcode)) {
			pdu.setSrcTermId(spcode);
		}else {
			pdu.setSrcTermId(queryMap.get("spcode"));
		}

		String attime = line.getOptionValue("attime");
		if (StringUtils.isNotBlank(attime)) {
			pdu.setAtTime(attime);
		}

		String msgsrc = line.getOptionValue("msgsrc");
		if (StringUtils.isNotBlank(msgsrc)) {
			pdu.setMsgSrc(msgsrc);
		} else {
			pdu.setMsgSrc(queryMap.get("msgsrc"));
		}

		String serviceid = queryMap.get("serviceid");
		if (StringUtils.isNotBlank(serviceid)) {
			pdu.setMServiceId(serviceid);
		}
		return pdu;
	}

	public EndpointEntity buildClient(Map<String, String> queryMap) {
		SMGPClientEndpointEntity client = new SMGPClientEndpointEntity();
		String userName = queryMap.get("username");
		String pass = queryMap.get("password");
		String version = queryMap.get("version");
		client.setClientID(userName);
		client.setPassword(pass);
		client.setClientVersion(Integer.valueOf(version).byteValue());
		return client;
	}

}

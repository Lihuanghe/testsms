package com.zx.testsms;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;

import com.chinamobile.cmos.sms.SmppSmsDcs;
import com.chinamobile.cmos.sms.SmsTextMessage;
import com.zx.sms.BaseMessage;
import com.zx.sms.codec.smpp.Address;
import com.zx.sms.codec.smpp.SmppSplitType;
import com.zx.sms.codec.smpp.msg.SubmitSm;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.smpp.SMPPClientEndpointEntity;

public class SmppProtocolProcessor implements ProtocolProcessor {

	public BaseMessage buildMsg(String telephone, String txt, CommandLine line, Map<String, String> queryMap) {

		SubmitSm pdu = new SubmitSm();
		pdu.setRegisteredDelivery((byte) 1);
		String content = "test-sms";
		if (StringUtils.isNotBlank(txt)) {
			content = txt;
		}
		String dcs = line.getOptionValue("dcs");
		if (StringUtils.isBlank(dcs)) {
			pdu.setSmsMsg(content);
		} else {
			pdu.setSmsMsg(new SmsTextMessage(content, new SmppSmsDcs(Byte.valueOf(dcs))));
		}
		byte ton = 2;
		byte npi = 1;
		if (StringUtils.isNotBlank(queryMap.get("ton"))) {
			ton = Integer.valueOf(queryMap.get("ton")).byteValue();
		}
		if (StringUtils.isNotBlank(queryMap.get("npi"))) {
			npi = Integer.valueOf(queryMap.get("npi")).byteValue();
		}

		String spcode = line.getOptionValue("spcode");
		if (StringUtils.isNotBlank(spcode)) {
			pdu.setSourceAddress(new Address(ton, npi, spcode));

		} else {
			pdu.setSourceAddress(new Address(ton, npi, queryMap.get("spcode")));
		}
		pdu.setPriority((byte) 1);

		if (StringUtils.isNotBlank(queryMap.get("servicetype"))) {
			pdu.setServiceType(queryMap.get("servicetype"));
		}
		String attime = line.getOptionValue("attime");
		if (StringUtils.isNotBlank(attime)) {
			pdu.setScheduleDeliveryTime(attime);
			pdu.setValidityPeriod(attime);
		}
		
        String pid = line.getOptionValue("pid");
        if(StringUtils.isNotBlank(pid)) {
        	pdu.setProtocolId((byte)Integer.parseInt(pid));
       }

		pdu.setDestAddress(new Address(ton, npi, telephone));

		return pdu;
	}

	public EndpointEntity buildClient(Map<String, String> queryMap) {

		SMPPClientEndpointEntity client = new SMPPClientEndpointEntity();
		String userName = queryMap.get("username");
		String pass = queryMap.get("password");
		String version = queryMap.get("version");
		String servicetype = queryMap.get("servicetype");
		String addzero = queryMap.get("addzero");
		String splittype =  queryMap.get("splittype");
		client.setSystemId(userName);
		client.setPassword(pass);
		client.setInterfaceVersion(Integer.valueOf(version).byteValue());
		client.setSplitType(SmppSplitType.valueOf(splittype));
		if (StringUtils.isNoneBlank(servicetype))
			client.setSystemType(servicetype);

		if (StringUtils.isNoneBlank(addzero))
			client.setAddZeroByte(true);
		return client;
	}
}

package com.zx.testsms;

import java.util.Map;

import org.apache.commons.cli.CommandLine;

import com.zx.sms.BaseMessage;
import com.zx.sms.connect.manager.EndpointEntity;

public interface ProtocolProcessor {

	BaseMessage buildMsg(String telephone,String txt ,CommandLine line,Map<String, String> queryMap);
	EndpointEntity buildClient(Map<String,String> queryMap);
}

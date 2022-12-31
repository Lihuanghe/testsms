package com.zx.testsms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinamobile.cmos.MessageReceiver;
import com.chinamobile.cmos.SmsClient;
import com.chinamobile.cmos.SmsClientBuilder;
import com.chinamobile.cmos.sms.SmppSmsDcs;
import com.chinamobile.cmos.sms.SmsDcs;
import com.zx.sms.BaseMessage;
import com.zx.sms.LongSMSMessage;
import com.zx.sms.codec.cmpp.wap.LongMessageFrame;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.EndpointEntity.ChannelType;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("c", true, "config File");
		options.addOption("h", false, "help info");
		options.addOption("sid", true, "serverUrl id config File");
		options.addOption("tel", true, "telephone");
		options.addOption("txt", true, "SMS Content");
		options.addOption("dcs", true, "msg-fmt");
		options.addOption("attime", true, "At_Time . yyMMddhhmmsstnnp . 220401150159032+");
		options.addOption("wait", true, "wait time to exit");
		options.addOption("spcode", true, "spcode");
		options.addOption("msgsrc", true, "msgsrc");
		options.addOption("pid", true, "pid");
		options.addOption("raw", true,
				"send  raw splited hex user-data , encode : dcs,ud,dcs,ud.  ex. 8,0500037702016cb3531777......");
		CommandLine line;
		try {
			line = new DefaultParser().parse(options, args);

			if (line.hasOption("h")) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("Options", options);
				return;
			}
			String config = line.getOptionValue("c","cfg.cfg");
			String serverId = line.getOptionValue("sid","smpp");
			Properties properties = loadProperties(config);
			String url = properties.getProperty(serverId);
			if (StringUtils.isBlank(url)) {
				System.out.println(serverId + " ServerUrl is blank!");
				return;
			}
			URI uri = URI.create(url);
			String protocol = uri.getScheme();

			ProtocolProcessor processor = null;
			Map<String, String> queryMap = queryToMap(uri.getQuery());
			if ("cmpp".equalsIgnoreCase(protocol)) {
				processor = new CmppProtocolProcessor();
			} else if ("smpp".equalsIgnoreCase(protocol)) {
				processor = new SmppProtocolProcessor();
			} else if ("sgip".equalsIgnoreCase(protocol)) {
				processor = new SgipProtocolProcessor();
			} else if ("smgp".equalsIgnoreCase(protocol)) {
				processor = new SMGPProtocolProcessor();
			}

			EndpointEntity client = processor.buildClient(queryMap);
			String proxy = queryMap.get("proxy");
			client.setId("c-" + serverId);
			client.setHost(uri.getHost());
			client.setPort(uri.getPort());
			client.setValid(true);
			client.setChannelType(ChannelType.DUPLEX);
			client.setMaxChannels((short) 1);
			client.setProxy(proxy);
			BeanUtils.copyProperties(client, queryMap);
			SmsClientBuilder builder = new SmsClientBuilder();
			final SmsClient smsClient = builder.entity(client).keepAllIdleConnection() // 保持空闲连接，以便能接收上行或者状态报告消息
					.window(32) // 设置发送窗口
					.receiver(new MessageReceiver() {

						public void receive(BaseMessage message) {
//							logger.info("receive : {}", message.toString());
						}
					}).build();
			BaseMessage msg = processor.buildMsg(line.getOptionValue("tel","13800138000"), line.getOptionValue("txt"), line,
					queryMap);

			if (line.hasOption("raw")) {
				String userDatas = line.getOptionValue("raw");
				if (StringUtils.isNoneBlank(userDatas)) {
					String[] b_ud = userDatas.split(",");
					List<LongMessageFrame> frames = new ArrayList<LongMessageFrame>();

					if (b_ud.length > 0 && b_ud.length % 2 != 0) {
						logger.error("raw user data length is odd ,must be even and greate than zero. ");
						return;
					}

					for (int i = 0; i < b_ud.length; i += 2) {
						String dcs = b_ud[i];
						String smsContent = b_ud[i + 1];
						byte[] b_smsContent = Hex.decodeHex(smsContent.toCharArray());
						LongMessageFrame frame = new LongMessageFrame();
						frame.setPkseq((byte) 0x54);
						frame.setPktotal((byte) (b_ud.length / 2));
						frame.setPknumber((byte) (i / 2 + 1));
						if ("smpp".equalsIgnoreCase(protocol)) {
							frame.setMsgfmt(new SmppSmsDcs(Byte.valueOf(dcs)));
						} else {
							frame.setMsgfmt(new SmsDcs(Byte.valueOf(dcs)));
						}
						frame.setTpudhi(b_ud.length > 2 ? (short) 1 : (short) 0);
						frame.setMsgLength((short) b_smsContent.length);
						frame.setMsgContentBytes(b_smsContent);
						frames.add(frame);
					}

					List<BaseMessage> msgs = new ArrayList<BaseMessage>();
					for (LongMessageFrame frame : frames) {
						BaseMessage basemsg = ((LongSMSMessage<BaseMessage>) msg).generateMessage(frame);
						BaseMessage response = smsClient.sendRawMsg(basemsg);
					}

				} else {
					logger.error("raw user data is empty.");
				}
			} else {
				BaseMessage response = smsClient.asyncSendJustInChannel(msg).get();
			}

			String wait = line.getOptionValue("wait", "3000");
			Thread.sleep(Integer.valueOf(wait));
			smsClient.close();
			Thread.sleep(Integer.valueOf(wait));

			logger.info("exit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, String> queryToMap(String query) {
		if (StringUtils.isBlank(query))
			return null;
		Map<String, String> result = new HashMap();
		String[] parameters = query.split("&");
		if (parameters.length > 0) {
			for (String pairs : parameters) {
				if (StringUtils.isBlank(pairs))
					continue;
				String[] kv = pairs.split("=");
				if (kv.length > 1) {
					result.put(kv[0], kv[1]);
				} else {
					result.put(kv[0], "");
				}
			}
		}
		return result;
	}

	private static Properties loadProperties(String resources) throws FileNotFoundException {
		InputStream inputstream = new FileInputStream(new File(resources));

		try {
			Properties properties = new Properties();

			properties.load(inputstream);

			return properties;

		} catch (IOException e) {

			throw new RuntimeException(e);

		} finally {

			try {

				inputstream.close();

			} catch (IOException e) {

				throw new RuntimeException(e);

			}

		}
	}
}

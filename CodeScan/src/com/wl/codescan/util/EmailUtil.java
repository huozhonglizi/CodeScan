package com.wl.codescan.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailUtil {

	/**
	 * 使用javaMail发送邮件
	 * 
	 * @param account
	 *            账户
	 * @param password
	 *            密码
	 * @param receiveAddress
	 *            收件人账号
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @param imagePaths
	 *            发送图片地址
	 */
	public static void shareEmail(String account, String password,
			List<String> receiveAddress, String subject, String content,
			List<String> imagePaths) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.126.com");
		properties.put("mail.smtp.auth", "true");

		MimeMessage message = new MimeMessage(Session.getInstance(properties,
				new Authenticator() {

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("", "");
					}

				}));
		try {
			message.setFrom(new InternetAddress(account));
			message.setRecipients(
					Message.RecipientType.TO,
					getInternetAddresses(receiveAddress).toArray(
							new InternetAddress[] {}));
			message.setSubject(subject);
			message.setText(content, "utf8");

			MimeMultipart multipart = new MimeMultipart();
			MimeBodyPart part = null;
			for (String path : imagePaths) {
				part = new MimeBodyPart();
				part.setDataHandler(new DataHandler(new FileDataSource(path)));
				multipart.addBodyPart(part);
			}
			message.setContent(multipart);
			message.saveChanges();
			Transport.send(message);

		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
	}

	private static List<InternetAddress> getInternetAddresses(
			List<String> addresses) {
		List<InternetAddress> internetAddresses = new ArrayList<InternetAddress>();
		try {
			for (String address : addresses) {
				internetAddresses.add(new InternetAddress(address));
			}
		} catch (Exception e) {
		}
		return internetAddresses;
	}
	
	
	

}

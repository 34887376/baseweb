package base.test.base.util;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateUUID {

	/**
	 * 获取原始的uuid
	 * @return
	 */
	public static String getOrginUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		return str;
	}
	
	/**
	 * 获取去除"-"的uuid
	 * @return
	 */
	public static String getOnlyCharUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		Pattern pattern = Pattern.compile("-");
		Matcher matcher = pattern.matcher(str);
		//替换符合正则的数据
		String reStr = matcher.replaceAll("");
		return reStr;
	}
	
	public static void main(String[] args) {
		GenerateUUID.getOnlyCharUUID();
	}
}

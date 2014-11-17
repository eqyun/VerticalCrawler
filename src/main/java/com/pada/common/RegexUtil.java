package com.pada.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	// Patterns
	// 整数或者是小数
	public static final String INT_AND_DOUBLE = "^[0-9]+\\.{0,1}[0-9]{0,2}$";
	// 全部是数字,不包括符号和小数点
	public static final String NUMBER = "^[0-9]*$";
	// 只能是0或者是非零开头的数字
	public static final String ZERO_OR_NON_ZERO = "^(0|[1-9][0-9]*)$";
	// 非零正整数
	public static final String POSITIVE_INT = "\\+?[1-9][0-9]*$";
	// 非零负整数
	public static final String NEGATIVE_INT = "\\-?[1-9][0-9]*$";
	// 字母
	public static final String LETTER = "^[A-Za-z]+$";
	// 大写字母
	public static final String UPPERCASE_LETTER = "^[A-Z]+$";
	// 小写字母
	public static final String LOWERCASE_LETTER = "^[a-z]+$";
	// 数字和字母
	public static final String NUMBER_AND_LETTER = "^[A-Za-z0-9]+";
	// 邮箱: example@host.xxx
	public static final String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	// 电话号码 012-87654321, 0123-87654321, 0123－7654321
	public static final String TELEPHONE_NUMBER = "\\d{4}-\\d{8}|\\d{4}-\\d{7}|\\d(3)-\\d(8)";
	// 手机号码 1XXXXXXXXXX
	public static final String PHONE_NUMBER = "^[1]\\d{10}";
	// 中文汉字
	public static final String CHINESE = "^[\u4e00-\u9fa5]+$";
	// HTML标签
	public static final String HTML_TAG = "<(\\S*?)[^>]*>.*?</\\1>|<.*? />";
	// 链接
	public static final String URL = "[a-zA-z]+://[^\\s]*";
	// IP地址
	public static final String IP_ADDRESS = "\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}";
	// 邮编
	public static final String ZIP_CODE = "[1-9]\\d{5}(?!\\d)";

	/**
	 *
	 * @param input
	 *            要检测的字符串
	 * @param regex
	 *            正则表达式
	 * @param length
	 *            是一个整型数组, 当有需要验证input的长度时传入. 12表示input的长度不能超过12, [6,
	 *            12]表示input的长度需要在6-12之间.
	 * @return
	 */
	public static boolean check(String input, String regex, Integer... length) {
		boolean result = false;
		switch (length.length) {
		case 0:
			result = input.matches(regex);
			break;
		case 1:
			result = input.matches(regex) && input.length() <= length[0];
			break;
		case 2:
			result = input.matches(regex) && input.length() >= length[0]
					&& input.length() <= length[1];
			break;
		default:
			result = false;
			break;
		}
		return result;
	}

	public static List<String> searchMatch(String str, String regex,
			Integer... groupNum) {
		List<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			switch (groupNum.length) {
			case 0:
				result.add(matcher.group());
				break;
			case 1:
				result.add(matcher.group(groupNum[0]));
				break;
			}
		}
		return result;
	}

}

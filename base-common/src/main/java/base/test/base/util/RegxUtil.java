package base.test.base.util;

public class RegxUtil {
	
	/**
	 * 校验email是否合法
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		String em = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		if(email.matches(em)){
			return true;
		}
		return false;
	}
	
	/**
	 * 校验电话号码是否合法
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone){
		String ph = "^[1][3578]\\d{9}$";
		if(phone.matches(ph)){
			return true;
		}
		return false;
	}
	
	/**
	 * 校验用户注册的用户名是否合法
	 * @param pin
	 * @return
	 */
	public static boolean isValidPin(String pin){
		String pinRegx = "^[a-zA-Z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5|\\-|\\_]{2,20}[a-zA-Z0-9\\u4e00-\\u9fa5]$";
		if(pin.matches(pinRegx)){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		boolean isemail = RegxUtil.isEmail("test@jd.com");
		System.out.println(isemail);
		boolean isPin1 = RegxUtil.isValidPin("sdsdsfsd");
		System.out.println(isPin1);
		boolean isPin = RegxUtil.isValidPin("sd中fdsafdsafdasf国道dkd_d-可道d");
		System.out.println(isPin);
	}
		
}

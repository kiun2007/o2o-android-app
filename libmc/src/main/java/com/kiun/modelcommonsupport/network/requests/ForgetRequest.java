package com.kiun.modelcommonsupport.network.requests;
import android.os.Parcel;
import android.os.Parcelable;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 忘记密码请求,修改密码.
 */
public class ForgetRequest extends MCBaseRequest implements Parcelable {

	/**
	  用户密码. 
	 */
	public String password;

	/**
	  用户手机号. 
	 */
	public String phone;

	/**
	  短信验证码. 
	 */
	public String code;

	/**
	 * 密码强度.
	 */
	public String pswStrength = "0";

	/**
	  短信验证码类型, 1用户验证码登入 2用户注册 3忘记密码 4银行卡验证. 
	 */
	public String codeType = "3";

	protected ForgetRequest(Parcel in) {
		password = in.readString();
		phone = in.readString();
		code = in.readString();
		codeType = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(password);
		dest.writeString(phone);
		dest.writeString(code);
		dest.writeString(codeType);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ForgetRequest> CREATOR = new Creator<ForgetRequest>() {
		@Override
		public ForgetRequest createFromParcel(Parcel in) {
			return new ForgetRequest(in);
		}

		@Override
		public ForgetRequest[] newArray(int size) {
			return new ForgetRequest[size];
		}
	};

	@Override
	public String requestPath() {
		return "user/userInfo/passwordForget";
	}

	public ForgetRequest(){
		super();
	}
}

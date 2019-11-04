package com.xzxx.decorate.o2o.requests;
import android.os.Parcel;
import android.os.Parcelable;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class RegisterRequest extends MCBaseRequest implements Parcelable {

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
	 短信验证码类型, 1用户验证码登入 2用户注册 3忘记密码 4银行卡验证.
	 */
	public String codeType = "2";

	protected RegisterRequest(Parcel in) {
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

	public static final Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
		@Override
		public RegisterRequest createFromParcel(Parcel in) {
			return new RegisterRequest(in);
		}

		@Override
		public RegisterRequest[] newArray(int size) {
			return new RegisterRequest[size];
		}
	};

	@Override
	public String requestPath() {
		return "user/userInfo/register";
	}

	public RegisterRequest(){
		super();
	}
}

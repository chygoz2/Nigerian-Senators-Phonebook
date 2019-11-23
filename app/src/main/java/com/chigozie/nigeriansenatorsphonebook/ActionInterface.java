package com.chigozie.nigeriansenatorsphonebook;

public interface ActionInterface {
    public void onPhoneCallClickListener(String phone);
    public void onEmailClickListener(String email, String name);
    public void onSmsClickListener(String phone, String text);
}

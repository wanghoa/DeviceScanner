package com.devicewifitracker.android.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object SendEmail {


    fun sendEmail(context: Context) {
        val email = Intent(Intent.ACTION_SEND)
//邮件发送类型：无附件，纯文本
        email.type = "plain/text"
//邮件接收者（数组，可以是多位接收者）
        val emailReciver = arrayOf(Constant.EMAIL)

        val emailTitle = ""// 主题
        val emailContent = ""// 邮件内容
//设置邮件地址

        email.putExtra(Intent.EXTRA_EMAIL, emailReciver)
//设置邮件标题

        email.putExtra(Intent.EXTRA_SUBJECT, emailTitle)
//设置发送的内容

        email.putExtra(Intent.EXTRA_TEXT, emailContent)
        //调用系统的邮件系统
        context.startActivity(Intent.createChooser(email, "请选择邮件发送软件"))
    }

    fun openMail(context: Context) {
        val uri = Uri.parse("mailto:" + Constant.EMAIL);
        val packageInfos = context.getPackageManager()
            .queryIntentActivities(Intent(Intent.ACTION_SENDTO, uri), 0); //List<ResolveInfo>
        val tempPkgNameList = ArrayList<String>();
        val emailIntents = ArrayList<Intent>();
        for (info in packageInfos) {
            val pkgName = info.activityInfo.packageName;
            if (!tempPkgNameList.contains(pkgName)) {
                tempPkgNameList.add(pkgName);
                val intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
                emailIntents.add(intent!!);
            }
        }
        if (!emailIntents.isEmpty()) {
//            String[] email = {"XXXXXX@qq.co"};
            val intent = Intent(Intent.ACTION_SENDTO, uri);
//            intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
            intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
            intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
//            val  chooserIntent = Intent.createChooser(intent, ResUtils.getString(R.string.select_mail_tips));
//            if (chooserIntent != null) {
//                startActivity(chooserIntent);
//            } else {
//                showToast(R.string.no_mail_app_tips);
//            }
//        } else {
//            showToast(R.string.no_mail_app_tips);
//        }
        }
    }


}
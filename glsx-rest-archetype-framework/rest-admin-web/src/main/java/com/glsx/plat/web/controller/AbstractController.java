package com.glsx.plat.web.controller;

import com.glsx.plat.common.enums.MimeType;
import com.glsx.plat.common.utils.DateUtils;
import com.glsx.plat.exception.SystemMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author payu
 */
@Slf4j
@Controller
public abstract class AbstractController {

    public static final String CHARSET_UTF8 = "UTF-8";

    @Resource
    protected HttpServletRequest request;

    /**
     * 获取会话用户
     *
     * @return
     */
    public abstract Object getSessionUser();

    public abstract String getAccount();

    public abstract Long getUserId();

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parse(text));
            }
        });
    }

    /**
     * 获取客户端Ip
     *
     * @return
     */
    protected String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 流预览
     *
     * @param input
     * @param mime
     * @param response
     * @throws IOException
     */
    protected void preview(InputStream input, MimeType mime, HttpServletResponse response) throws IOException {
        if (input == null) return;

        if (mime == MimeType.AUTO) {
            throw SystemMessage.NOT_SUPPORT_OPERATOR.exception("预览需要设置 mime 类型,无法自动获取");
        }
        response.setContentType(mime.getContentType());
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        ServletOutputStream output = response.getOutputStream();
        IOUtils.copy(input, output);
        output.flush();
    }

    /**
     * 下载流
     *
     * @param input
     * @param mime
     * @param fileName
     * @param response
     * @throws IOException
     */
    protected void download(InputStream input, MimeType mime, String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (input == null) {
            return;
        }
        boolean isAuto = false;
        if (mime == MimeType.AUTO) {
            String extension = FilenameUtils.getExtension(fileName);
            mime = MimeType.parseMIME(extension);
            isAuto = true;
        }
        response.setContentType(mime.getContentType());
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        String suffix = mime.getSuffix();

        String encodeFileName = encodeFilename(request, fileName);
        if (StringUtils.isNotBlank(suffix) && !isAuto) {
            encodeFileName += ("." + mime.getSuffix());
        }

        response.setHeader("Content-Disposition", "attachment;filename=\"" + encodeFileName + "\"");
        long length = input.available();
        if (length != -1 && length != 0) {
            response.setContentLength((int) length);
        }
        ServletOutputStream output = response.getOutputStream();
        IOUtils.copy(input, output);
        output.flush();
    }

    /**
     * 编码文件名
     *
     * @param request
     * @param filename
     * @return
     */
    public String encodeFilename(HttpServletRequest request, String filename) {
        /**
         * 获取客户端浏览器和操作系统信息
         * 在IE浏览器中得到的是：User-Agent=Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Maxthon; Alexa Toolbar)
         * 在Firefox中得到的是：User-Agent=Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.7.10) Gecko/20050717 Firefox/1.0.6
         */
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String agent = request.getHeader("USER-AGENT");
        try {
            if ((agent != null) && (agent.contains("MSIE"))) {
                String newFileName = URLEncoder.encode(filename, CHARSET_UTF8);
                newFileName = StringUtils.replace(newFileName, "+", "%20");
                if (newFileName.length() > 150) {
                    newFileName = new String(filename.getBytes("GB2312"), "ISO8859-1");
                    newFileName = StringUtils.replace(newFileName, " ", "%20");
                }
                return newFileName;
            }
            if ((agent != null) && (agent.contains("Mozilla"))) {
                return new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            }
            if ((agent != null) && (agent.contains("PostmanRuntime"))) {
                return new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            }
            return filename;
        } catch (Exception ex) {
            return filename;
        }
    }

}

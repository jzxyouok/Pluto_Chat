package com.wl.pluto.plutochat.entity;

import java.io.InputStream;

public class FileData {

    /** 上传文件数据 */
    private byte[] data;

    /** 输入流 */
    private InputStream inputStream;

    /** 文件路径 */
    private String filePath;

    /** 上传文件名称 */
    private String fileName;

    /** 请求参数名称 */
    private String formName;

    /** 上传文件类型 */
    private String contentType = "application/octet-stream";

    /** 以二进制流的形式构造数据 */
    public FileData(byte[] data, String fileName, String formName, String type) {

	this.data = data;
	this.fileName = fileName;
	this.formName = formName;
	if (type != null) {

	    this.contentType = type;
	}
    }

    /** 以文件流的形式构造数据 */
    public FileData(InputStream inputStream, String fileName, String formName,
	    String type) {
	this.inputStream = inputStream;
	this.fileName = fileName;
	this.formName = formName;
	if (type != null) {
	    this.contentType = type;
	}
    }

    public byte[] getData() {
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }

    public InputStream getInputStream() {
	return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
	this.inputStream = inputStream;
    }

    public String getFilePath() {
	return filePath;
    }

    public void setFilePath(String filePath) {
	this.filePath = filePath;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public String getFormName() {
	return formName;
    }

    public void setFormName(String formName) {
	this.formName = formName;
    }

    public String getContentType() {
	return contentType;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }

}

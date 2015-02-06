/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enterprisegeeks.ws.data;

/**
 * ファイル情報
 */
public class FileAttr implements TextBase{
    public final String name;
    public final String fileName;
    public final String type;
    
    public FileAttr(String name,String fileName, String type) {
        this.name = name;
        this.fileName = fileName;
        this.type = type;
    }
}

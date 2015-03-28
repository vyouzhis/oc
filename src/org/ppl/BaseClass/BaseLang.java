package org.ppl.BaseClass;

import org.ppl.core.PObject;
import org.ppl.etc.globale_config;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public abstract class BaseLang extends PObject implements BaseLangInterface {
	private String StdName=null;
		
	public void setStdName(String stdName) {
		StdName = "_"+stdName;
	}
	
	@Override
	public String _Lang(String key) {
		// TODO Auto-generated method stub
		if(StdName == null){
			StdName = "_"+SliceName(stdClass);
		}
		
		if(StdName.equals("_common")){
			return _CLang(key);
		}
		
		String val = _MLang(key);
		if(val==null){
			return _CLang(key);
		}
		
		return val;
	}

	public String _CLang(String key) {
		String stdName = "_common";
		
		return find(key, stdName);
	}
	
	public String _MLang(String key) {

		if(StdName == null){
			StdName = "_"+SliceName(stdClass);
		}
		return find(key, StdName);
	}
	
	private String find(String key, String stdsName) {
		//System.out.println("name:"+stdName+" key:"+key);
		Injector injector = globale_config.injector;
		LibLang libLan = (LibLang) injector.getInstance(Key.get(
				LibLang.class, Names.named(stdsName)));
		
		String lan = libLan._Lang(key);
		if(lan==null) return "lang key:"+key+" is not exist!";
		return lan;
	}
	
	
}

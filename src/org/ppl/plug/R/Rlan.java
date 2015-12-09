package org.ppl.plug.R;

import org.ppl.etc.globale_config;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class Rlan {
	public Rlan() {
		// TODO Auto-generated constructor stub		
		if(globale_config.rcoonnect==null){
			try {
				globale_config.rcoonnect = new RConnection();
			} catch (RserveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	public double lm(double[] dataX, double[] dataY) {
		double Rres = 0;
		try {
			globale_config.rcoonnect.assign("dataX", dataX);
			globale_config.rcoonnect.assign("dataY", dataY);

			globale_config.rcoonnect.voidEval("lm.r<-lm(dataX ~ dataY)");

			Rres = (globale_config.rcoonnect.eval("lm.r$coefficients[[1]]").asDouble());
		} catch (REngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Rres;
	}
	
	public String[] ls() {
		try {
			String[] Search = globale_config.rcoonnect.eval("search()").asStrings();	
			String sR[][] =  new String[Search.length][] ;
			for (int i = 0; i < Search.length; i++) {
				
				String[] sRutils = globale_config.rcoonnect.eval("ls('"+Search[i]+"')").asStrings();
				 sR[i] = sRutils;
			}
			
			int m = 0;
			for (int i = 0; i < sR.length; i++) {
				for (int j = 0; j < sR[i].length; j++) {
					//echo(sR[i][j]);
					m++;
				}
			}
			String[] res = new String[m];
			m=0;
			for (int i = 0; i < sR.length; i++) {
				for (int j = 0; j < sR[i].length; j++) {
					res [m] = sR[i][j];
					m++;
				}
			}
			
			return res;
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

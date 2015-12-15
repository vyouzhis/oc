package org.ppl.plug.R;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class Rlan {
	static RConnection rcoonnect = null;

	static Rlan rl = null;

	public static Rlan getInstance() {
		if (rl == null) {
			rl = new Rlan();
			
		}
		rl.connection();
		return rl;
	}
	
	public void close() {
		rcoonnect.close();
		rcoonnect = null;
	}
	
	public RConnection connection() {
		if (rcoonnect == null) {
			try {
				rcoonnect = new RConnection();
			} catch (RserveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rcoonnect;
	}
	
	public double lm(double[] dataX, double[] dataY) {
		double Rres = 0;
		try {
			rcoonnect.assign("dataX", dataX);
			rcoonnect.assign("dataY", dataY);

			rcoonnect.voidEval("lm.r<-lm(dataX ~ dataY)");

			Rres = (rcoonnect.eval("lm.r$coefficients[[1]]")
					.asDouble());
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
			String[] Search = rcoonnect.eval("search()")
					.asStrings();
			String sR[][] = new String[Search.length][];
			for (int i = 0; i < Search.length; i++) {

				String[] sRutils = rcoonnect.eval(
						"ls('" + Search[i] + "')").asStrings();
				sR[i] = sRutils;
			}

			int m = 0;
			for (int i = 0; i < sR.length; i++) {
				for (int j = 0; j < sR[i].length; j++) {
					// echo(sR[i][j]);
					m++;
				}
			}
			String[] res = new String[m];
			m = 0;
			for (int i = 0; i < sR.length; i++) {
				for (int j = 0; j < sR[i].length; j++) {
					res[m] = sR[i][j];
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

	public String lsUserInstallPack() {
		String script = "ip <- as.data.frame(installed.packages()[,c(1,3:4)]) "
				+ "rownames(ip) <- NULL"
				+ "ip <- ip[is.na(ip$Priority),1:2,drop=FALSE]"
				+ "print(ip, row.names=FALSE)";

		return "";
	}
}

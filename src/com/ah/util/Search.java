package com.ah.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Date;

import org.netlib.blas.DGEMV;
import org.netlib.blas.DNRM2;
import org.netlib.blas.DTRSV;
import org.netlib.blas.Dnrm2;
import org.netlib.blas.Dtrsv;
import org.netlib.lapack.DGELS;
import org.netlib.lapack.DGEQP3;
import org.netlib.lapack.DGEQRF;
import org.netlib.lapack.DORMQR;
import org.netlib.lapack.DTRTRS;
import org.netlib.lapack.Dgecon;
import org.netlib.lapack.Dgels;
import org.netlib.lapack.Dgeqp3;
import org.netlib.lapack.Dgeqrf;
import org.netlib.lapack.Dgesv;
import org.netlib.lapack.Dlange;
import org.netlib.lapack.Dormqr;
import org.netlib.lapack.Dsygv;
import org.netlib.lapack.Dtrtrs;
import org.netlib.util.doubleW;
import org.netlib.util.intW;

public class Search {
	private static final Tracer log = new Tracer(Search.class, "locationlog");

	private static final Tracer logt = new Tracer(Search.class, "tracerlog");

	public static void fillOval(Graphics2D g2, Point2D p, double scale) {
		int x = (int) Math.round(p.getX() * scale);
		int y = (int) Math.round(p.getY() * scale);
		int edge = 5;
		g2.fillOval(x - edge, y - edge, edge * 2, edge * 2);
	}

	public static void lm(double[][] a, String name) {
		log.info_ln(name + " = [");
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				String fmt = "%4$12.8f";
				log.info_non(String.format(fmt, name, i, j, a[i][j]));
			}
			log.info_ln(";");
		}
		log.info_ln("];");
	}

	public static void lv(double[] a, String name) {
		log.info_non(name + " = [");
		for (int i = 0; i < a.length; i++) {
			String fmt = "%.8f; ";
			log.info_non(String.format(fmt, a[i]));
		}
		log.info_ln("];");
	}

	private static class SegmentsContext {
		double rx, ry, tx, ty, th, distanceToTransmitter, actualDistance, loss,
				elevationRatio, startX, startY;

		int row, column, equationIndex, gridColumns;

		double[][] lossExponents, A;

		boolean verbose;
	}

	private static double frequenciesBG[] = { 2412, // 0
			2412, // 1
			2417, // 2
			2422, // 3
			2427, // 4
			2432, // 5
			2437, // 6
			2442, // 7
			2447, // 8
			2452, // 9
			2457, // 10
			2462, // 11
			2462, // 12
			2462, // 13
			2462 // 14
	};

	private static double frequenciesA[] = { 5180, // 36
			5180, // 36 7
			5180, // 36 8
			5180, // 36 9
			5200, // 40
			5200, // 40 1
			5200, // 40 2
			5200, // 40 3
			5220, // 44
			5220, // 44 5
			5220, // 44 6
			5220, // 44 7
			5240, // 48
			5240, // 48 9
			5240, // 48 0
			5240, // 48 1
			5260, // 52
			5260, // 52 3
			5260, // 52 4
			5260, // 52 5
			5280, // 56
			5280, // 56 7
			5280, // 56 8
			5280, // 56 9
			5300, // 60
			5300, // 60 1
			5300, // 60 2
			5300, // 60 3
			5320, // 64
			5320, // 64 5
			5320, // 64 6
			5320, // 64 7
			5320, // 64 8
			5320, // 64 9
			5320, // 64 0
			5320, // 64 1
			5320, // 64 2
			5320, // 64 3
			5320, // 64 4
			5320, // 64 5
			5320, // 64 6
			5320, // 64 7
			5320, // 64 8
			5320, // 64 9
			5320, // 64 0
			5320, // 64 1
			5320, // 64 2
			5320, // 64 3
			5320, // 64 4
			5320, // 64 5
			5320, // 64 6
			5320, // 64 7
			5320, // 64 8
			5320, // 64 9
			5320, // 64 0
			5320, // 64 1
			5320, // 64 2
			5320, // 64 3
			5320, // 64 4
			5320, // 64 5
			5320, // 64 6
			5320, // 64 7
			5320, // 64 8
			5320, // 64 9
			5500, // 100
			5500, // 100 1
			5500, // 100 2
			5500, // 100 3
			5520, // 104
			5520, // 104 5
			5520, // 104 6
			5520, // 104 7
			5540, // 108
			5540, // 108 9
			5540, // 108 0
			5540, // 108 1
			5560, // 112
			5560, // 112 3
			5560, // 112 4
			5560, // 112 5
			5580, // 116
			5580, // 116 7
			5580, // 116 8
			5580, // 116 9
			5600, // 120
			5600, // 120 1
			5600, // 120 2
			5600, // 120 3
			5620, // 124
			5620, // 124 5
			5620, // 124 6
			5620, // 124 7
			5640, // 128
			5640, // 128 9
			5640, // 128 0
			5640, // 128 1
			5660, // 132
			5660, // 132 3
			5660, // 132 4
			5660, // 132 5
			5680, // 136
			5680, // 136 7
			5680, // 136 8
			5680, // 136 9
			5700, // 140
			5700, // 140 1
			5700, // 140 2
			5700, // 140 3
			5700, // 140 4
			5700, // 140 5
			5700, // 140 6
			5700, // 140 7
			5700, // 140 8
			5745, // 149
			5745, // 149 0
			5745, // 149 1
			5745, // 149 2
			5765 // 153
	};

	public static double getFrequency(short channel) {
		if (channel < 15) {
			// b/g
			return frequenciesBG[channel];
		}
		if (channel >= 36 && channel <= 153) {
			return frequenciesA[channel - 36];
		} else {
			return 5500;
		}
	}

	public static class ClientDetected {
		private String clientMac;

		private Date detectedTime;

		private int rssi;

		private short channel;

		private double c;

		private double xm, ym;

		private double plf;

		private double attenuation;

		private double distance;

		private long id;

		public int apIndex;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public double getAttenuation() {
			return attenuation;
		}

		public void setAttenuation(double attenuation) {
			this.attenuation = attenuation;
		}

		public double getPlf() {
			return plf;
		}

		public void setPlf(double plf) {
			this.plf = plf;
		}

		public double getXm() {
			return xm;
		}

		public void setXm(double xm) {
			this.xm = xm;
		}

		public double getYm() {
			return ym;
		}

		public void setYm(double ym) {
			this.ym = ym;
		}

		public String getClientMac() {
			return clientMac;
		}

		public void setClientMac(String clientMac) {
			this.clientMac = clientMac;
		}

		public int getRssi() {
			if (rssi > -35) {
				if (rssi - 5 > -35) {
					return -35;
				} else {
					return rssi - 5;
				}
			} else {
				return rssi;
			}
		}

		public String getRssiString() {
			return Integer.toString(rssi);
		}

		public void setRssi(int rssi) {
			this.rssi = rssi;
		}

		public short getChannel() {
			return channel;
		}

		public void setChannel(short channel) {
			this.channel = channel;
		}

		public double getC() {
			return c;
		}

		public void setC(double c) {
			this.c = c;
		}

		public Date getDetectedTime() {
			return detectedTime;
		}

		public void setDetectedTime(Date detectedTime) {
			this.detectedTime = detectedTime;
		}
	}

	public static class WallLoss {
		public double x1, y1, x2, y2, width, absorption;
	}

	/*
	 * Iterative weighted SR-LS algorithm
	 */
	public static double[] lstr(ClientDetected[] clients, double metricToImage,
			double plf, double erp, double h2, double sqrtGamma, int maxIters,
			double tol, Graphics2D g2, int[] iterations, boolean verbose)
			throws Exception {
		double FN = plf / 2;
		int m = clients.length;

		double[] x = null;
		int iter = 0;
		double e = Math.log(10) / FN;
		double[] d = new double[m];
		for (int i = 0; i < m; i++) {
			ClientDetected client = clients[i];
			d[i] = Math.pow(10, (erp - client.getC() - client.getRssi()) / FN);
		}
		// A = [ diag(sqrt(w)) * [ -2*p, -2*q, a, ones(m,1) ] ;
		// sqrt(gamma) * [ 0, 0, 1, 0 ] ]
		int n = 4;
		double A[][] = new double[m + 1][n];
		// b = [ diag(sqrt(w)) * (d - p.^2 - q.^2); sqrt(gamma)*15 ]
		double[] b = new double[m + 1];
		for (iter = 1; iter <= maxIters; iter++) {
			for (int i = 0; i < m; i++) {
				ClientDetected client = clients[i];
				double sw = 1 / (e * d[i]);
				double beta = (1 - e
						* (FN * Math.log10(d[i]) + client.getC() + client
								.getRssi()))
						* d[i];
				A[i][0] = -2 * client.getXm() * sw;
				A[i][1] = -2 * client.getYm() * sw;
				A[i][2] = -1;
				A[i][3] = sw;
				b[i] = (beta - Math.pow(client.getXm(), 2)
						- Math.pow(client.getYm(), 2) - h2)
						* sw;
			}
			A[m][2] = sqrtGamma;
			b[m] = sqrtGamma * erp;
			x = wsrls(A, b);
			if (x == null) {
				return null;
			}
			if (g2 != null) {
				Point2D be = new Point2D.Double(x[0], x[1]);
				g2.setColor(Color.red);
				fillOval(g2, be, metricToImage);
			}
			// r = [ x(3) - 5*N*log10(d) - C - rssi; sqrt(gamma)*(x(3) - 15)
			// ];
			// A = [ [ (10*N/log(10)) * (p-x(1)) ./d, ...
			// (10*N/log(10)) * (q-x(2)) ./d, ones(m,1) ];
			// 0, 0, sqrt(gamma) ];
			double[] r = new double[m + 1];
			for (int i = 0; i < m; i++) {
				ClientDetected client = clients[i];
				double f = (2 * FN / Math.log(10)) / d[i];
				A[i][0] = f * (client.getXm() - x[0]);
				A[i][1] = f * (client.getYm() - x[1]);
				A[i][2] = 1;
				r[i] = x[2] - FN * Math.log10(d[i]) - client.getC()
						- client.getRssi();
			}
			// g = 2*A'*r;
			r[m] = sqrtGamma * (x[2] - erp);
			double norm = 0;
			for (int i = 0; i < 3; i++) {
				double g = 0;
				for (int j = 0; j < m + 1; j++) {
					g += A[j][i] * r[j];
				}
				norm += Math.pow(g * 2, 2);
			}
			norm = Math.sqrt(norm);
			if (verbose) {
				log.info_ln("x = [" + x[0] + "; " + x[1] + "; " + x[2]
						+ "]; % it " + iter + ", norm: "
						+ String.format("%.5f", norm));
			}
			if (norm < tol) {
				break;
			}
			for (int i = 0; i < m; i++) {
				ClientDetected client = clients[i];
				d[i] = Math.pow(x[0] - client.getXm(), 2)
						+ Math.pow(x[1] - client.getYm(), 2) + h2;
			}
		}
		iterations[0] = iter;

		if (x == null) {
			return null;
		}

		if (g2 != null) {
			Point2D be = new Point2D.Double(x[0], x[1]);
			g2.setColor(Color.black);
			fillOval(g2, be, metricToImage);
		}
		return x;
	}

	private static double[] wsrls(double[][] A, double[] b) {
		int m = A.length;
		int n = A[0].length;

		// A'*A;
		double AtA[] = new double[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j <= i; j++) {
				// Only lower triangle (j <= i)
				for (int k = 0; k < m; k++) {
					AtA[i + j * n] += A[k][i] * A[k][j];
				}
			}
		}

		// D = diag( [1; 1; 0] );
		// D will turn into R
		double R[] = new double[n * n];
		R[0] = 1;
		R[n + 1] = 1;

		// [R, S] = eig(D, A'*A)
		intW info = new intW(1);
		double[] work = new double[1];
		// Query for optimal work size
		// DSYGV.DSYGV(1, "V", "L", n, R, AtA, null, work, -1, info);
		Dsygv.dsygv(1, "V", "L", n, R, 0, n, AtA, 0, n, null, 0, work, 0, -1,
				info);
		int lwork = (int) work[0];
		work = new double[lwork];
		double[] lambda = new double[n];
		// DSYGV.DSYGV(1, "V", "L", n, R, AtA, lambda, work, lwork, info);
		Dsygv.dsygv(1, "V", "L", n, R, 0, n, AtA, 0, n, lambda, 0, work, 0,
				lwork, info);
		if (info.val != 0) {
			log.info_ln("% DSYGV failed: " + info.val + ", lwork: " + lwork);
			return null;
		}

		// A'*b
		double[] Atb = new double[n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Atb[i] += A[j][i] * b[j];
			}
		}
		// bb = R'*A'*b;
		// ff = R'*f
		double[] bb = new double[n];
		double[] ff = new double[n];
		for (int i = 0; i < n; i++) {
			ff[i] = -R[(i + 1) * n - 1] / 2;
			for (int j = 0; j < n; j++) {
				bb[i] += R[j + i * n] * Atb[j];
			}
		}

		double tmin = -1 / max(lambda);

		double t = tmin + 0.01 * Math.abs(tmin);
		double[] y = new double[n];
		for (int iter = 1; iter <= 100; iter++) {
			double phi = 0;
			for (int i = 0; i < n; i++) {
				// y = (bb - t*ff) ./ (1.0 + t*lambda);
				y[i] = (bb[i] - t * ff[i]) / (1 + t * lambda[i]);
				// phi = y'*S*y + 2 * ff'*y;
				phi += Math.pow(y[i], 2) * lambda[i] + 2 * ff[i] * y[i];
			}
			if (Math.abs(phi) < 1e-5) {
				break;
			}
			double dphi = 0;
			for (int i = 0; i < n; i++) {
				// dy = - (ff + bb.*lambda) ./ (1.0 + t*lambda).^2;
				double dy = -(ff[i] + bb[i] * lambda[i])
						/ Math.pow(1 + t * lambda[i], 2);
				// dphi = 2 * (S*y + ff)' * dy;
				dphi += (lambda[i] * y[i] + ff[i]) * dy;
			}
			t -= phi / (2 * dphi);
		}

		// x = R * y;
		double[] x = new double[3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < n; j++) {
				x[i] += R[i + j * n] * y[j];
			}
		}
		return x;
	}

	private static double max(double a[]) {
		double max = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

	public static int lsgnbt(ClientDetected[] nodes, Graphics2D g2,
			double metricToImage, double[] x, double lossExponent, double h2,
			int gnIterations, double gammaTxPower, double startingTxPower,
			double precision, boolean verbose) {
		int btSteps = 0;
		double t = 1;
		for (int iteration = 0; iteration < gnIterations; iteration++) {
			double x1 = x[0];
			double x2 = x[1];
			double x3 = x[2];
			if (g2 != null) {
				Point2D be = new Point2D.Double(x[0], x[1]);
				if (iteration == 0) {
					g2.setColor(Color.yellow);
				} else {
					g2.setColor(Color.blue);
				}
				fillOval(g2, be, metricToImage);
			}
			int m = nodes.length + 1;
			int n = 3;
			double av[] = new double[m * n];
			double drc = -lossExponent / Math.log(10);
			for (int i = 0; i < m - 1; i++) {
				ClientDetected client = nodes[i];
				double p = client.getXm();
				double q = client.getYm();
				double ds = Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2) + h2;
				av[i] = drc * (x1 - p) / ds;
				av[i + m] = drc * (x2 - q) / ds;
				av[i + 2 * m] = 1;
			}
			double gammaSqrt = Math.sqrt(gammaTxPower);
			// Eerste afgeleide
			av[m - 1 + 2 * m] = gammaSqrt;
			double[] r = getR(nodes, x, lossExponent, h2, gammaTxPower,
					startingTxPower);

			double[] atr = new double[n];
			// ATR = 2 * A' * R
			for (int j = 0; j < n; j++) {
				double s = 0;
				for (int i = 0; i < m; i++) {
					s += av[i + j * m] * r[i];
				}
				atr[j] = 2 * s;
			}
			double norm2 = Dnrm2.dnrm2(n, atr, 0, 1);
			if (verbose) {
				log.info_ln("x = " + x1 + "; " + x2 + "; " + x3 + "; % it "
						+ iteration + ", BT (" + btSteps + ", " + t
						+ "), norm: " + String.format("%.5f", norm2));
			}
			if (Double.isNaN(norm2)) {
				return -iteration;
			} else if (norm2 < precision) {
				return iteration;
			}

			int nrhs = 1;
			double[] v = new double[m];
			// v = - r
			for (int i = 0; i < m; i++) {
				v[i] = -r[i];
			}

			intW info = new intW(1);
			double[] work = new double[1];
			// DGELS.DGELS("N", m, n, nrhs, a, v, work, -1, info);
			Dgels.dgels("N", m, n, nrhs, null, 0, m, null, 0, m, work, 0, -1,
					info);
			int lwork = (int) work[0];
			work = new double[lwork];
			// DGELS.DGELS("N", m, n, nrhs, a, v, work, lwork, info);
			Dgels.dgels("N", m, n, nrhs, av, 0, m, v, 0, m, work, 0, lwork,
					info);
			if (info.val < 0) {
				log.info_ln("% Illegal value for argument: " + -info.val);
				return -iteration;
			}
			if (!vlss(v)) {
				return -iteration;
			}

			t = 1;
			btSteps = 0;
			double normR = normSquare(r);
			double rav = get2rav(atr, v);

			double[] newx = getNewX(x, t, v, n);
			double[] newr = getR(nodes, newx, lossExponent, h2, gammaTxPower,
					startingTxPower);

			double alpha2rav = 0.01 * rav;
			while (normSquare(newr) > normR + alpha2rav * t && btSteps < 50) {
				btSteps++;
				t /= 2;
				newx = getNewX(x, t, v, n);
				newr = getR(nodes, newx, lossExponent, h2, gammaTxPower,
						startingTxPower);
			}
			x[0] = newx[0];
			x[1] = newx[1];
			x[2] = newx[2];
		}
		return -gnIterations;
	}

	public static double normSquare(double[] v) {
		double ns = 0;
		for (int i = 0; i < v.length; i++) {
			ns += v[i] * v[i];
		}
		return ns;
	}

	public static int lsgnbtm(ClientDetected[] nodes, Graphics2D g2,
			double metricToImage, double[] x, double lossExponent, double h2,
			int gnIterations, double gammaTxPower, double startingTxPower,
			double precision) {
		int btSteps = 0;
		double t = 1;
		for (int iteration = 0; iteration < gnIterations; iteration++) {
			double x1 = x[0];
			double x2 = x[1];
			double x3 = x[2];
			log.debug("lsgnbtm", "Iteration: " + iteration + ", (" + x1 + ", "
					+ x2 + ", " + x3 + "), BT steps: " + btSteps + ", t: " + t);
			if (g2 != null) {
				Point2D be = new Point2D.Double(x[0], x[1]);
				if (iteration == 0) {
					g2.setColor(Color.yellow);
				} else {
					g2.setColor(Color.blue);
				}
				fillOval(g2, be, metricToImage);
			}
			int m = nodes.length + 1;
			int n = 3;
			double[][] a = new double[m][n];
			double drc = -lossExponent / Math.log(10);
			for (int i = 0; i < m - 1; i++) {
				ClientDetected client = nodes[i];
				double p = client.getXm();
				double q = client.getYm();
				double ds = Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2) + h2;
				a[i][0] = drc * (x1 - p) / ds;
				a[i][1] = drc * (x2 - q) / ds;
				a[i][2] = 1;
			}
			double gammaSqrt = Math.sqrt(gammaTxPower);
			// Eerste afgeleide
			a[m - 1][0] = 0;
			a[m - 1][1] = 0;
			a[m - 1][2] = gammaSqrt;
			double[] r = getR(nodes, x, lossExponent, h2, gammaTxPower,
					startingTxPower);

			double beta = 0;
			double[] atr = new double[n];
			// ATR = 2 * A' * R
			DGEMV.DGEMV("T", m, n, 2, a, r, 1, beta, atr, 1);
			double norm2 = DNRM2.DNRM2(n, atr, 1);
			log.debug("lsgnbtm", "Norm2: " + norm2);
			if (Double.isNaN(norm2)) {
				return -1;
			} else if (norm2 < precision) {
				return iteration;
			}

			int nrhs = 1;
			double[][] v = new double[m][nrhs];
			// v = - r
			for (int i = 0; i < m; i++) {
				v[i][0] = -r[i];
			}

			intW info = new intW(1);
			double[] work = new double[1];
			DGELS.DGELS("N", m, n, nrhs, a, v, work, -1, info);
			int lwork = (int) work[0];
			work = new double[lwork];
			DGELS.DGELS("N", m, n, nrhs, a, v, work, lwork, info);
			log.debug("lsgnbtm", "Work: " + lwork + ", result code: "
					+ info.val);
			if (info.val < 0) {
				log.info_ln("% Illegal value for argument: " + -info.val);
				return -1;
			}
			if (!vlss(v)) {
				return -1;
			}

			t = 1;
			btSteps = 0;
			double normR = Math.pow(DNRM2.DNRM2(m, r, 1), 2);
			double rav = get2rav(atr, v);

			double[] newx = getNewX(x, t, v, n);
			double[] newr = getR(nodes, newx, lossExponent, h2, gammaTxPower,
					startingTxPower);

			double alpha2rav = 0.01 * rav;
			while (Math.pow(DNRM2.DNRM2(m, newr, 1), 2) > normR + alpha2rav * t
					&& btSteps < 50) {
				btSteps++;
				t /= 2;
				newx = getNewX(x, t, v, n);
				newr = getR(nodes, newx, lossExponent, h2, gammaTxPower,
						startingTxPower);
			}
			x[0] = newx[0];
			x[1] = newx[1];
			x[2] = newx[2];
		}
		return -gnIterations;
	}

	/*
	 * Assumes matrix A has full rank
	 */
	private boolean gnRssiBT(Graphics2D g2, ClientDetected[] nodes, double[] x,
			boolean useA, double lossExponent, double h2, int gnIterations,
			double startingTxPower, double precision) {
		int btSteps = 0;
		double t = 1;
		for (int iteration = 0; iteration < gnIterations; iteration++) {
			double x1 = x[0];
			double x2 = x[1];
			double x3 = x[2];
			logt.info("gnRssiBT", "Iteration: " + iteration + ", (" + x1 + ", "
					+ x2 + ", " + x3 + "), BT steps: " + btSteps + ", t: " + t);
			int m = nodes.length;
			int n = 2;
			double[][] a = new double[m][n];
			double drc = -lossExponent / Math.log(10);
			for (int i = 0; i < m; i++) {
				ClientDetected client = nodes[i];
				double p = client.getXm();
				double q = client.getYm();
				double ds = Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2) + h2;
				a[i][0] = drc * (x1 - p) / ds;
				a[i][1] = drc * (x2 - q) / ds;
			}
			double[] r = getR(nodes, x, lossExponent, h2, m);

			double beta = 0;
			double[] atr = new double[n];
			// ATR = 2 * A' * R
			DGEMV.DGEMV("T", m, n, 2, a, r, 1, beta, atr, 1);
			double norm2 = DNRM2.DNRM2(n, atr, 1);
			log.debug("gnRssiBT", "Norm2: " + norm2);
			if (Double.isNaN(norm2)) {
				return false;
			} else if (norm2 < precision) {
				return true;
			}

			int nrhs = 1;
			double[][] v = new double[m][nrhs];
			// v = - r
			for (int i = 0; i < m; i++) {
				v[i][0] = -r[i];
			}

			intW info = new intW(1);
			double[] work = new double[1];
			DGELS.DGELS("N", m, n, nrhs, a, v, work, -1, info);
			int lwork = (int) work[0];
			work = new double[lwork];
			DGELS.DGELS("N", m, n, nrhs, a, v, work, lwork, info);
			log.debug("gnRssiBT", "Work: " + lwork + ", result code: "
					+ info.val);
			if (info.val < 0) {
				log.info("gnRssiBT", "Illegal value for argument: " + -info.val);
				return false;
			}
			if (!vlss(v)) {
				return false;
			}

			t = 1;
			btSteps = 0;
			double normR = Math.pow(DNRM2.DNRM2(m, r, 1), 2);
			double rav = get2rav(atr, v);

			double[] newx = getNewX(x, t, v, n);
			newx[2] = startingTxPower;
			double[] newr = getR(nodes, newx, lossExponent, h2, m);

			double alpha2rav = 0.01 * rav;
			while (Math.pow(DNRM2.DNRM2(m, newr, 1), 2) > normR + alpha2rav * t
					&& btSteps < 50) {
				btSteps++;
				t /= 2;
				newx = getNewX(x, t, v, n);
				newx[2] = startingTxPower;
				newr = getR(nodes, newx, lossExponent, h2, m);
			}
			x[0] = newx[0];
			x[1] = newx[1];
			x[2] = newx[2];
		}
		return false;
	}

	public static boolean vlss(double[][] b) {
		double x1 = b[0][0];
		double x2 = b[1][0];
		if (Double.isNaN(x1) || Double.isNaN(x2)) {
			logt.info("vlss", "Solution is invalid: (" + x1 + ", " + x2 + ")");
			return false;
		} else if (Double.isInfinite(x1) || Double.isInfinite(x2)) {
			logt.info("vlss", "Infinite solution is invalid: (" + x1 + ", "
					+ x2 + ")");
			return false;
		}
		return true;
	}

	public static boolean vlss(double[] b) {
		double x1 = b[0];
		double x2 = b[1];
		if (Double.isNaN(x1) || Double.isNaN(x2)) {
			logt.info("vlss", "Solution is invalid: (" + x1 + ", " + x2 + ")");
			return false;
		} else if (Double.isInfinite(x1) || Double.isInfinite(x2)) {
			logt.info("vlss", "Infinite solution is invalid: (" + x1 + ", "
					+ x2 + ")");
			return false;
		}
		return true;
	}

	private static double get2rav(double[] atr, double[][] v) {
		int n = atr.length;
		double rav = 0;
		for (int i = 0; i < n; i++) {
			rav += atr[i] * v[i][0];
		}
		return rav;
	}

	private static double get2rav(double[] atr, double[] v) {
		int n = atr.length;
		double rav = 0;
		for (int i = 0; i < n; i++) {
			rav += atr[i] * v[i];
		}
		return rav;
	}

	/*
	 * newx = x + t * v
	 */
	private static double[] getNewX(double[] x, double t, double[][] v, int n) {
		double[] newx = new double[x.length];
		// newx = x + t * v
		for (int i = 0; i < n; i++) {
			newx[i] = x[i] + t * v[i][0];
		}
		return newx;
	}

	private static double[] getNewX(double[] x, double t, double[] v, int n) {
		double[] newx = new double[x.length];
		// newx = x + t * v
		for (int i = 0; i < n; i++) {
			newx[i] = x[i] + t * v[i];
		}
		return newx;
	}

	private static double[] getR(ClientDetected[] nodes, double[] x,
			double lossExponent, double h2, int m) {
		double x1 = x[0];
		double x2 = x[1];
		// Not a constant anymore
		double clientPowerLevel = x[2];
		log.debug("getR", "(" + x1 + ", " + x2 + ", " + clientPowerLevel + ")");
		double[] r = new double[m];
		for (int i = 0; i < nodes.length; i++) {
			ClientDetected client = nodes[i];
			double p = client.getXm();
			double q = client.getYm();
			double ds = Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2) + h2;
			double pathLoss = 0;
			pathLoss = getPathLoss(ds, client.getC(), lossExponent / 2);
			double estimatedRssi = clientPowerLevel - pathLoss;
			log.debug("getR", "Estimated RSSI: " + estimatedRssi
					+ ", measured RSSI: " + client.getRssi());
			r[i] = estimatedRssi - client.getRssi();
		}
		return r;
	}

	private static double[] getR(ClientDetected[] nodes, double[] x,
			double lossExponent, double h2, double gammaTxPower,
			double startingTxPower) {
		int m = nodes.length + 1;
		double[] r = getR(nodes, x, lossExponent, h2, m);
		double gammaSqrt = Math.sqrt(gammaTxPower);
		// Extra term in de kostfunktie
		r[m - 1] = gammaSqrt * (x[2] - startingTxPower);
		return r;
	}

	private static double getPathLoss(double distance, double c, double n) {
		return c + n * Math.log10(distance);
	}

	public static void hmle(double[] apX, double[] apY, double[] apPower,
			short[] apChannel, double apElevation, double[] gridX,
			double[] gridY, double[][] lossExponents, boolean useA,
			double mapWidthMetric, double mapHeightMetric, double squareSize,
			double imageWidth, double imageHeight, short mapColors[][],
			short mapChannels[][], short shadesPerColor) {
		double C = useA ? 20 * Math.log10(5500) - 28
				: 20 * Math.log10(2400) - 28;

		double imageToMetric = mapWidthMetric / imageWidth;
		SegmentsContext context = new SegmentsContext();
		context.verbose = false;
		context.lossExponents = lossExponents;
		context.A = null;

		int column = 0;
		double gridRight = gridX[column + 1];
		for (double x = 0; x < imageWidth; x += 1) {
			context.rx = (x + 0.5) * imageToMetric;
			while (context.rx >= gridRight) {
				gridRight = gridX[++column + 1];
			}
			int row = 0;
			double gridBottom = gridY[row + 1];
			for (double y = 0; y < imageHeight; y += 1) {
				context.ry = (y + 0.5) * imageToMetric;
				while (context.ry >= gridBottom) {
					gridBottom = gridY[++row + 1];
				}
				double maxRssi = -100000;
				short channel = -1;
				for (int i = 0; i < apX.length; i++) {
					if (apPower[i] > 0) {
						context.column = column;
						context.row = row;
						context.tx = apX[i];
						context.ty = apY[i];
						context.th = apElevation;
						segments(context, gridX, gridY);
						double rssi = apPower[i] - (C + 10 * context.loss);
						if (rssi > maxRssi) {
							maxRssi = rssi;
							channel = apChannel[i];
						}
					}
				}
				short rssiColor = -1;
				if (maxRssi != -100000) {
					if (maxRssi > -35) {
						maxRssi = -35;
					}
					rssiColor = (short) (-35 - maxRssi);
					if (rssiColor >= shadesPerColor) {
						rssiColor = -1;
					}
				}
				mapColors[(int) x][(int) y] = rssiColor;
				mapChannels[(int) x][(int) y] = channel;
			}
		}
	}

	/*
	 * Create a grid that avoids AP positions.
	 */
	public static double[] sgrd(double mapSize, double squareSize,
			double[] apPositions) {
		double margin = squareSize * 0.3;
		int gridLines = (int) (mapSize / squareSize) + 1;
		if ((gridLines - 1) * squareSize < mapSize) {
			gridLines++;
		}
		log.debug("sgrd", "# grid lines: " + gridLines);
		double[] spacedGrid = new double[gridLines];
		for (int i = 0; i < gridLines; i++) {
			spacedGrid[i] = i * squareSize;
		}
		if (spacedGrid[gridLines - 1] == mapSize) {
			spacedGrid[gridLines - 1] += 1;
		}
		double[] sortedApPositions = apPositions.clone();
		java.util.Arrays.sort(sortedApPositions);
		if (adjustSpacing(spacedGrid, sortedApPositions, margin)) {
			int mergedGrids = mergeGrids(spacedGrid, sortedApPositions, margin);
			log.debug("sgrd", "# grid lines removed: " + mergedGrids);
			// Just for verification
			if (adjustSpacing(spacedGrid, sortedApPositions, margin)) {
				log.error("sgrd",
						"No adjustments should be required at this point.");
				return null;
			}
		}
		return spacedGrid;
	}

	public static boolean adjustSpacing(double[] spacedGrid,
			double[] apPositions, double margin) {
		int apIndex = 0;
		int gridIndex = 0;
		boolean adjustSpacing = false;
		while (apIndex < apPositions.length) {
			while (spacedGrid[gridIndex] < apPositions[apIndex]) {
				gridIndex = gridIndex + 1;
			}
			while (apIndex < apPositions.length
					&& apPositions[apIndex] < spacedGrid[gridIndex]) {
				double left = apPositions[apIndex] - spacedGrid[gridIndex - 1];
				double right = spacedGrid[gridIndex] - apPositions[apIndex];
				String message = null;
				if (left + 1e-10 < margin && gridIndex > 1) {
					// AP is too close to left edge, so moving left grid
					// line further to the left
					spacedGrid[gridIndex - 1] = apPositions[apIndex] - margin;
					adjustSpacing = true;
					message = ", moving left edge to: "
							+ spacedGrid[gridIndex - 1];
				} else if (right + 1e-10 < margin) {
					// AP is too close to right edge, so moving right grid
					// line further to the right
					spacedGrid[gridIndex] = apPositions[apIndex] + margin;
					adjustSpacing = true;
					message = ", moving right edge to: "
							+ spacedGrid[gridIndex];
				} else {
					message = ", margins OK.";
				}
				log.debug("adjustSpacing", "AP: " + apPositions[apIndex]
						+ ", left: " + left + ", right: " + right + message);
				apIndex = apIndex + 1;
			}
		}
		return adjustSpacing;
	}

	public static int mergeGrids(double[] spacedGrid, double[] apPositions,
			double margin) {
		int apIndex = 0;
		int gridIndex = 0;
		int mergeGrids = 0;
		while (apIndex < apPositions.length) {
			while (spacedGrid[gridIndex] < apPositions[apIndex]) {
				gridIndex = gridIndex + 1;
			}
			while (apIndex < apPositions.length
					&& apPositions[apIndex] < spacedGrid[gridIndex]) {
				double right = spacedGrid[gridIndex] - apPositions[apIndex];
				if (right + 1e-10 < margin) {
					// AP is too close to the right edge, but the right edge
					// must have been moved to the left during adjustSpacing
					// so this grid line must go.
					log.debug("mergeGrids", "Line " + spacedGrid[gridIndex]
							+ " needs to go.");
					mergeGrids++;
					for (int i = gridIndex; i < spacedGrid.length - 1; i++) {
						spacedGrid[i] = spacedGrid[i + 1];
					}
					spacedGrid[spacedGrid.length - 1] = -1;
				}
				apIndex = apIndex + 1;
			}
		}
		return mergeGrids;
	}

	/*
	 * Least square estimate of loss exponent.
	 */
	public static double lsle(double[] apX, double[] apY, double[] apPower,
			short[] apChannel, int[][] neighborRSSI) {
		int numberOfMeasurements = apX.length * (apX.length - 1);
		double[][] A = new double[numberOfMeasurements][1];
		double[] b = new double[numberOfMeasurements];
		int equationIndex = 0;
		for (int i = 0; i < apX.length; i++) {
			double rx = apX[i];
			double ry = apY[i];
			for (int j = 0; j < apX.length; j++) {
				if (i == j || neighborRSSI[i][j] == 0) {
					continue;
				}
				double floorDistance = Math.pow(rx - apX[j], 2)
						+ Math.pow(ry - apY[j], 2);
				double actualDistance = Math.sqrt(floorDistance);// assume all
																	// APs are
																	// at the
																	// same
																	// height
				if (actualDistance > 1) {
					A[equationIndex][0] = Math.log10(actualDistance);
					double C = 20 * Math.log10(getFrequency(apChannel[j])) - 28;
					b[equationIndex] = (apPower[j] - neighborRSSI[i][j] - C) / 10;
					equationIndex++;
				}
			}
		}
		log.debug("lsle", "Actual number of measurements: " + equationIndex
				+ " out of: " + numberOfMeasurements);

		Date start = new Date();
		int[] jpvt = fitLoss(A, b);
		Date end = new Date();
		long ms = end.getTime() - start.getTime();
		log.debug("lsle", "Overdetermined system with " + b.length
				+ " equations and 1 variable solved in " + ms + " ms.");
		if (jpvt == null) {
			return 3.2;
		}
		return b[0];
	}

	/*
	 * Least square estimate of loss exponent.
	 */
	public static double lsle(double[] RX, double[] RY, double[] TX,
			double[] TY, double[] TH, double[] power, int[] frequency,
			int[] RSSI) {
		int numberOfMeasurements = RX.length;
		double[][] A = new double[numberOfMeasurements][1];
		double[] b = new double[numberOfMeasurements];
		int equationIndex = 0;
		for (int i = 0; i < RX.length; i++) {
			double floorDistance = Math.pow(RX[i] - TX[i], 2)
					+ Math.pow(RY[i] - TY[i], 2);
			double actualDistance = Math.sqrt(floorDistance
					+ Math.pow(TH[i], 2));
			if (actualDistance > 1) {
				A[equationIndex][0] = Math.log10(actualDistance);
				double C = 20 * Math.log10(frequency[i]) - 28;
				b[equationIndex] = (power[i] - RSSI[i] - C) / 10;
				equationIndex++;
			}
		}
		logt.info("lsle", "Actual number of measurements: " + equationIndex
				+ " out of: " + numberOfMeasurements);

		Date start = new Date();
		int[] jpvt = fitLoss(A, b);
		Date end = new Date();
		long ms = end.getTime() - start.getTime();
		logt.info("lsle", "Overdetermined system with " + b.length
				+ " equations and 1 variable solved in " + ms + " ms.");
		if (jpvt == null) {
			return 3.2;
		}
		return b[0];
	}

	public static double[][] clbrle(double[] apX, double[] apY,
			double[] apPower, short[] apChannel, double[] gridX,
			double[] gridY, int[][] neighborRSSI, double defaultLE) {
		SegmentsContext context = new SegmentsContext();
		context.verbose = false;
		context.lossExponents = null;
		context.gridColumns = (gridX.length - 1);
		while (gridX[context.gridColumns] < 0) {
			context.gridColumns--;
		}
		int gridRows = gridY.length - 1;
		while (gridY[gridRows] < 0) {
			gridRows--;
		}
		int gridCount = gridRows * context.gridColumns;
		int numberOfMeasurements = apX.length * (apX.length - 1);
		int numberOfEquations = numberOfMeasurements + gridCount;
		if (numberOfMeasurements == 0) {
			numberOfEquations++;
		} else {
			numberOfEquations += apX.length * 12;
		}
		context.A = new double[numberOfEquations][gridCount];
		double[] b = new double[numberOfEquations];
		context.equationIndex = 0;
		for (int i = 0; i < apX.length; i++) {
			context.rx = apX[i];
			context.ry = apY[i];
			int column = gridIndex(context.rx, gridX);
			int row = gridIndex(context.ry, gridY);
			for (int j = 0; j < apX.length; j++) {
				if (i == j || neighborRSSI[i][j] == 0) {
					continue;
				}
				context.column = column;
				context.row = row;
				context.tx = apX[j];
				context.ty = apY[j];
				context.th = 0; // assume all APs are at the same height
				segments(context, gridX, gridY);
				double C = 20 * Math.log10(getFrequency(apChannel[j])) - 28;
				b[context.equationIndex] = (apPower[j] - neighborRSSI[i][j] - C) / 10;
				context.equationIndex++;
			}
		}
		logt.info("clbrle", "Actual number of measurements: "
				+ context.equationIndex + " out of: " + numberOfMeasurements);
		if (context.equationIndex == 0) {
			context.A[0][0] = 10;
			b[0] = defaultLE;
			context.equationIndex++;
		} else {
			findNeighbors(context, apX, apY, gridX, gridY, gridRows,
					context.gridColumns);
		}
		double gammaAverage = 0.1;
		double sqrtGammaAverage = Math.sqrt(gammaAverage);
		double average = 1.0 / gridCount;
		for (int i = 0; i < gridCount; i++) {
			for (int j = 0; j < gridCount; j++) {
				if (i == j) {
					context.A[context.equationIndex][j] = sqrtGammaAverage
							* (1 - average);
				} else {
					context.A[context.equationIndex][j] = -average
							* sqrtGammaAverage;
				}
			}
			context.equationIndex++;
		}

		Date start = new Date();
		int[] jpvt = fitLoss(context.A, b);
		Date end = new Date();
		long ms = end.getTime() - start.getTime();
		logt.info("clbrle", "Overdetermined system with " + b.length
				+ " equations and " + gridCount + " variables solved in " + ms
				+ " ms.");
		if (jpvt == null) {
			return null;
		}

		double[][] lossExponents = new double[gridRows][context.gridColumns];

		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (int i = 0; i < gridCount; i++) {
			double le = b[i];
			if (le < min) {
				min = le;
			}
			if (le > max) {
				max = le;
			}
			lossExponents[(jpvt[i] - 1) / context.gridColumns][(jpvt[i] - 1)
					% context.gridColumns] = le;
		}
		logt.info("clbrle", "lossExponents min: " + min + ", max: " + max);

		context.lossExponents = lossExponents;
		context.A = null;
		verifyLossExponentsFit(context, apX, apY, apPower, apChannel, gridX,
				gridY, neighborRSSI);

		return context.lossExponents;
	}

	private static void findNeighbors(SegmentsContext context, double[] apX,
			double[] apY, double[] gridX, double[] gridY, int gridRows,
			int gridColumns) {
		if (gridX.length == 2 && gridY.length == 2) {
			return;
		}
		logt.info("findNeighbors", "grids (" + gridRows + "x" + gridColumns
				+ ")");
		for (int i = 0; i < apX.length; i++) {
			int column = gridIndex(apX[i], gridX);
			int row = gridIndex(apY[i], gridY);
			int fromRow = row == 0 ? 0 : row - 1;
			int fromColumn = column == 0 ? 0 : column - 1;
			int toRow = row == gridRows - 1 ? row : row + 1;
			int toColumn = column == gridColumns - 1 ? column : column + 1;
			logt.info("findNeighbors", "AP(" + apX[i] + ", " + apY[i]
					+ ") at: (" + column + ", " + row + "), match from: ("
					+ fromColumn + ", " + fromRow + "), to: (" + toColumn
					+ ", " + toRow + ")");
			matchNeighbors(context, fromRow, fromColumn, toRow, toColumn);
		}
	}

	private static void matchNeighbors(SegmentsContext context, int fromRow,
			int fromColumn, int toRow, int toColumn) {
		double gammaNeighbors = 0.1;
		double sqrtGammaNeighbors = Math.sqrt(gammaNeighbors);
		for (int column = fromColumn; column < toColumn; column++) {
			for (int row = fromRow; row <= toRow; row++) {
				log.debug("matchNeighbors", "match (" + column + ", " + row
						+ ") with (" + (column + 1) + ", " + row + ")");
				context.A[context.equationIndex][row * context.gridColumns
						+ column] = sqrtGammaNeighbors;
				context.A[context.equationIndex][row * context.gridColumns
						+ column + 1] = -sqrtGammaNeighbors;
				context.equationIndex++;
			}
		}
		for (int column = fromColumn; column <= toColumn; column++) {
			for (int row = fromRow; row < toRow; row++) {
				log.debug("matchNeighbors", "match (" + column + ", " + row
						+ ") with (" + column + ", " + (row + 1) + ")");
				context.A[context.equationIndex][row * context.gridColumns
						+ column] = sqrtGammaNeighbors;
				context.A[context.equationIndex][(row + 1)
						* context.gridColumns + column + 1] = -sqrtGammaNeighbors;
				context.equationIndex++;
			}
		}
	}

	private static boolean fitLossQR(double[][] A, double[] b) {
		int m = A.length;
		int n = A[0].length;
		double[] AV = new double[m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				AV[i + j * m] = A[i][j];
			}
		}
		// A = Q*R
		double[] work = new double[1];
		intW info = new intW(1);
		double[] tau = new double[n];
		Dgeqrf.dgeqrf(m, n, null, 0, m, null, 0, work, 0, -1, info);
		if (info.val != 0) {
			log.error("fitLossQR", "Error in query QR factorization DGEQRF.");
			return false;
		}
		int lwork = (int) work[0];
		work = new double[lwork];
		logt.debug("fitLossQR", "Optimal DGEQRF work size: " + lwork);
		Dgeqrf.dgeqrf(m, n, AV, 0, m, tau, 0, work, 0, lwork, info);
		if (info.val != 0) {
			log.error("fitLossQR", "Error in QR factorization DGEQRF.");
			return false;
		}

		// Q'*b
		work = new double[1];
		info = new intW(1);
		Dormqr.dormqr("L", "T", m, 1, n, null, 0, m, null, 0, null, 0, m, work,
				0, -1, info);
		if (info.val != 0) {
			log.error("fitLossQR", "Error in query DORMQR.");
			return false;
		}
		lwork = (int) work[0];
		work = new double[lwork];
		logt.debug("fitLossQR", "Optimal DORMQR work size: " + lwork);
		Dormqr.dormqr("L", "T", m, 1, n, AV, 0, m, tau, 0, b, 0, m, work, 0,
				lwork, info);
		if (info.val != 0) {
			log.error("fitLossQR", "Error in DORMQR.");
			return false;
		}

		Dtrsv.dtrsv("U", "N", "N", n, AV, 0, m, b, 0, 1);
		return true;
	}

	private static boolean fitLossQR2d(double[][] A, double[] bv) {
		int m = A.length;
		int n = A[0].length;
		double[][] b = new double[m][1];
		for (int i = 0; i < m; i++) {
			b[i][0] = bv[i];
		}
		// A = Q*R
		double[] work = new double[1];
		intW info = new intW(1);
		double[] tau = new double[n];
		DGEQRF.DGEQRF(m, n, A, tau, work, -1, info);
		if (info.val != 0) {
			log.error("fitLossQR2d", "Error in query QR factorization DGEQRF.");
			return false;
		}
		int lwork = (int) work[0];
		work = new double[lwork];
		logt.info("fitLossQR2d", "Optimal DGEQRF work size: " + lwork);
		DGEQRF.DGEQRF(m, n, A, tau, work, lwork, info);
		if (info.val != 0) {
			log.error("fitLossQR2d", "Error in QR factorization DGEQRF.");
			return false;
		}
		// Q'*b
		work = new double[1];
		info = new intW(1);
		DORMQR.DORMQR("L", "T", m, 1, n, A, tau, b, work, -1, info);
		if (info.val != 0) {
			log.error("fitLossQR2d", "Error in query DORMQR.");
			return false;
		}
		lwork = (int) work[0];
		work = new double[lwork];
		log.debug("fitLossQR2d", "Optimal DORMQR work size: " + lwork);
		DORMQR.DORMQR("L", "T", m, 1, n, A, tau, b, work, lwork, info);
		if (info.val != 0) {
			log.error("fitLossQR2d", "Error in DORMQR.");
			return false;
		}
		for (int i = 0; i < m; i++) {
			bv[i] = b[i][0];
		}
		DTRSV.DTRSV("U", "N", "N", n, A, bv, 1);
		return true;
	}

	private static int[] fitLoss(double[][] A, double[] b) {
		int m = A.length;
		int n = A[0].length;
		double[] AV = new double[m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				AV[i + j * m] = A[i][j];
			}
		}

		// A*P = Q*R
		double[] work = new double[1];
		intW info = new intW(1);
		Dgeqp3.dgeqp3(m, n, null, 0, m, null, 0, null, 0, work, 0, -1, info);
		if (info.val != 0) {
			log.error("fitLoss", "Error in query QR factorization.");
			return null;
		}
		int lwork = (int) work[0];
		work = new double[lwork];
		log.debug("fitLoss", "Optimal DGEQP3 work size: " + lwork);
		double[] tau = new double[n];
		int[] jpvt = new int[n];
		Dgeqp3.dgeqp3(m, n, AV, 0, m, jpvt, 0, tau, 0, work, 0, lwork, info);
		if (info.val != 0) {
			log.error("fitLoss", "Error in QR factorization.");
			return null;
		}

		// Q'*b
		work = new double[1];
		info = new intW(1);
		Dormqr.dormqr("L", "T", m, 1, n, null, 0, m, null, 0, null, 0, m, work,
				0, -1, info);
		if (info.val != 0) {
			log.error("fitLoss", "Error in query DORMQR.");
			return null;
		}
		lwork = (int) work[0];
		work = new double[lwork];
		log.debug("fitLoss", "Optimal DORMQR work size: " + lwork);
		Dormqr.dormqr("L", "T", m, 1, n, AV, 0, m, tau, 0, b, 0, m, work, 0,
				lwork, info);
		if (info.val != 0) {
			log.error("fitLoss", "Error in DORMQR.");
			return null;
		}

		// R\Q'*b
		info = new intW(1);
		Dtrtrs.dtrtrs("U", "N", "N", n, 1, AV, 0, m, b, 0, m, info);
		if (info.val != 0) {
			log.error("fitLoss", "Error in DTRTRS.");
			return null;
		}
		return jpvt;
	}

	private static int[] fitLoss2D(double[][] A, double[] bv) {
		int m = A.length;
		int n = A[0].length;
		double[][] b = new double[m][1];
		for (int i = 0; i < m; i++) {
			b[i][0] = bv[i];
		}
		// A*P = Q*R
		double[] work = new double[1];
		intW info = new intW(1);
		DGEQP3.DGEQP3(m, n, A, null, null, work, -1, info);
		if (info.val != 0) {
			log.error("fitLoss2D", "Error in query QR factorization.");
			return null;
		}
		int lwork = (int) work[0];
		work = new double[lwork];
		log.debug("fitLoss2D", "Optimal DGEQP3 work size: " + lwork);
		double[] tau = new double[n];
		int[] jpvt = new int[n];
		DGEQP3.DGEQP3(m, n, A, jpvt, tau, work, lwork, info);
		if (info.val != 0) {
			log.error("fitLoss2D", "Error in QR factorization.");
			return null;
		}

		// Q'*b
		work = new double[1];
		info = new intW(1);
		DORMQR.DORMQR("L", "T", m, 1, n, A, tau, b, work, -1, info);
		if (info.val != 0) {
			log.error("fitLoss2D", "Error in query DORMQR.");
			return null;
		}
		lwork = (int) work[0];
		work = new double[lwork];
		log.debug("fitLoss2D", "Optimal DORMQR work size: " + lwork);
		DORMQR.DORMQR("L", "T", m, 1, n, A, tau, b, work, lwork, info);
		if (info.val != 0) {
			log.error("fitLoss2D", "Error in DORMQR.");
			return null;
		}

		// R\Q'*b
		info = new intW(1);
		DTRTRS.DTRTRS("U", "N", "N", n, 1, A, b, info);
		if (info.val != 0) {
			log.error("fitLoss2D", "Error in DTRTRS.");
			return null;
		}
		for (int i = 0; i < m; i++) {
			bv[i] = b[i][0];
		}
		return jpvt;
	}

	private static void verifyLossExponentsFit(SegmentsContext context,
			double[] apX, double[] apY, double[] apPower, short[] apChannel,
			double[] gridX, double[] gridY, int[][] neighborRSSI) {
		int numberOfMeasurements = 0;
		double cumulativeError = 0;
		for (int i = 0; i < apX.length; i++) {
			context.rx = apX[i];
			context.ry = apY[i];
			int column = gridIndex(context.rx, gridX);
			int row = gridIndex(context.ry, gridY);
			for (int j = 0; j < apX.length; j++) {
				if (i == j || neighborRSSI[i][j] == 0) {
					continue;
				}
				numberOfMeasurements++;
				context.column = column;
				context.row = row;
				context.tx = apX[j];
				context.ty = apY[j];
				context.th = 0; // assume all APs are at the same height
				segments(context, gridX, gridY);
				double measuredRssi = neighborRSSI[i][j];
				double C = 20 * Math.log10(getFrequency(apChannel[j])) - 28;
				double estimatedRssi = apPower[j] - (C + 10 * context.loss);
				cumulativeError += Math.abs(estimatedRssi - measuredRssi);
				log.debug("verifyLossExponentsFit", "Measured RSSI: "
						+ measuredRssi + ", Segmented RSSI: " + estimatedRssi);
			}
		}
		if (numberOfMeasurements == 0) {
			return;
		}
		logt.info("verifyLossExponentsFit",
				"Diff measured and segmented RSSI for " + numberOfMeasurements
						+ " measurements: " + cumulativeError
						+ ", per measurement: " + cumulativeError
						/ numberOfMeasurements);
	}

	public static void verifyMaxRssi(double rx, double ry, double[] apX,
			double[] apY, double[] apPower, short[] apChannel,
			double apElevation, double[] gridX, double[] gridY,
			double[][] lossExponents, boolean useA) {
		double C = useA ? 20 * Math.log10(5500) - 28
				: 20 * Math.log10(2400) - 28;
		int column = gridIndex(rx, gridX);
		int row = gridIndex(ry, gridY);
		SegmentsContext context = new SegmentsContext();
		context.lossExponents = lossExponents;
		context.rx = rx;
		context.ry = ry;
		double maxRssi = -100000;
		int maxIndex = 0;
		for (int i = 0; i < apX.length; i++) {
			context.tx = apX[i];
			context.ty = apY[i];
			context.column = column;
			context.row = row;
			context.th = apElevation; // assume all APs are at the same height
			segments(context, gridX, gridY);
			double estimatedRssi = apPower[i] - (C + 10 * context.loss);
			logt.info("verifyMaxRssi", "Estimated RSSI for channel "
					+ apChannel[i] + " is: " + estimatedRssi);
			if (estimatedRssi > maxRssi) {
				maxRssi = estimatedRssi;
				maxIndex = i;
			}
		}
		logt.info("verifyMaxRssi", "Channel " + apChannel[maxIndex]
				+ " has max RSSI: " + maxRssi);
	}

	private static int gridIndex(double r, double[] gridLines) {
		int gridIndex = 0;
		while (r >= gridLines[gridIndex]) {
			gridIndex++;
		}
		return --gridIndex;
	}

	private static void segments(SegmentsContext context, double[] gridX,
			double[] gridY) {
		double floorDistance = Math.pow(context.rx - context.tx, 2)
				+ Math.pow(context.ry - context.ty, 2);
		double actualDistance = Math.sqrt(floorDistance
				+ Math.pow(context.th, 2));
		floorDistance = Math.sqrt(floorDistance);
		if (context.verbose) {
			logt.info("segments", "r(" + context.rx + ", " + context.ry
					+ "), t(" + context.tx + ", " + context.ty + ", "
					+ context.th + "), floor distance: " + floorDistance
					+ ", actual distance: " + actualDistance);
		}
		context.elevationRatio = context.th / floorDistance;
		context.distanceToTransmitter = actualDistance;
		context.actualDistance = 0;
		context.loss = 0;

		double ax = (context.tx - context.rx) / (context.ry - context.ty);
		double bx = ax * context.ry + context.rx;
		double ay = (context.ty - context.ry) / (context.rx - context.tx);
		double by = ay * context.rx + context.ry;
		double gridLeft = gridX[context.column];
		double gridRight = gridX[context.column + 1];
		double gridTop = gridY[context.row];
		double gridBottom = gridY[context.row + 1];
		context.startX = context.rx;
		context.startY = context.ry;

		if (context.ty > context.ry) {
			// Line goes down
			if (context.tx > gridRight) {
				// Line crosses at least 1 grid line on the right
				do {
					double intersectionY = by - ay * gridRight;
					// Segments intersecting gridBottom directly, before
					// reaching gridRight.
					// | r |
					// | \ |
					// -+----\----+- = gridBottom
					// | \ |
					// | \ |
					// -+-------\-|-
					// | \|
					// | \ = (gridRight, intersectionY)
					// -+---------+\
					// | | t
					// gridBottomSegments
					while (gridBottom < intersectionY) {
						// Crossing gridBottom
						addSegment(context, bx - ax * gridBottom, gridBottom);
						// move down 1 grid.
						context.row = context.row + 1;
						gridBottom = gridY[context.row + 1];
					}
					// Segment intersecting gridRight, starts at gridBottom or
					// below if there are no gridBottom segments.
					// | r |
					// | \ |
					// -+------\--+- = gridBottom
					// | \ |
					// | \|
					// | \
					// | |\
					// -+---------+ \
					// | | \
					// | | t
					// gridRightSegment
					addSegment(context, gridRight, intersectionY);
					// move 1 grid to the right.
					context.column = context.column + 1;
					gridRight = gridX[context.column + 1];
				} while (gridRight < context.tx);
			} else if (context.tx < gridLeft) {
				// Line crosses at least 1 grid line on the left
				do {
					double intersectionY = by - ay * gridLeft;
					// Segments intersecting gridBottom directly, before
					// reaching gridLeft.
					// | r |
					// | / |
					// gridBottom = -+----/----+-
					// | / |
					// | / |
					// -+-/-------|-
					// |/ |
					// (gridLeft, intersectionY) = / |
					// /+---------+
					// t | |
					// gridBottomSegments
					while (gridBottom < intersectionY) {
						// Crossing gridBottom
						addSegment(context, bx - ax * gridBottom, gridBottom);
						// move down 1 grid.
						context.row = context.row + 1;
						gridBottom = gridY[context.row + 1];
					}
					// Segment intersecting gridLeft, starts at gridBottom or
					// below if there are no gridBottom segments.
					// | r |
					// | / |
					// -+--/------+- = gridBottom
					// | / |
					// |/ |
					// / |
					// /| |
					// /-+---------+-
					// / | |
					// t | | t
					// gridLeftSegment
					addSegment(context, gridLeft, intersectionY);
					// move 1 grid to the left.
					context.column = context.column - 1;
					gridLeft = gridX[context.column];
				} while (gridLeft > context.tx);
			}
			// Segments intersecting gridBottom, but line ends before reaching
			// gridLeft or gridRight.
			// | r |
			// | \ |
			// -+---\-----+- = gridBottom
			// | \ |
			// | \ |
			// -+------\--|-
			// | t |
			// | |
			// -+---------+-
			// | |
			// gridBottomSegments
			while (gridBottom < context.ty) {
				// Crossing gridBottom
				addSegment(context, bx - ax * gridBottom, gridBottom);
				// move down 1 grid.
				context.row = context.row + 1;
				gridBottom = gridY[context.row + 1];
			}
		} else {
			// Line goes up or horizontal.
			if (context.tx > gridRight) {
				// Line crosses at least 1 grid line on the right
				do {
					double intersectionY = by - ay * gridRight;
					// Segments intersecting gridTop directly, before
					// reaching gridRight.
					// | | t
					// -+---------+/
					// | / = (gridRight, intersectionY)
					// | /|
					// -+-------/-|-
					// | / |
					// | / |
					// -+----/----+- = gridTop
					// | / |
					// | r |
					// gridTopSegments
					while (gridTop > intersectionY) {
						addSegment(context, bx - ax * gridTop, gridTop);
						// move up 1 grid.
						context.row = context.row - 1;
						gridTop = gridY[context.row];
					}
					// Segment intersecting gridRight, starts at gridTop or
					// above if there are no gridTop segments.
					// | | t
					// | | /
					// -+---------+ /
					// | |/
					// | /
					// | /|
					// | / |
					// -+------/--+- = gridTop
					// | / |
					// | r |
					// gridRightSegment
					addSegment(context, gridRight, intersectionY);
					// move 1 grid to the right.
					context.column = context.column + 1;
					gridRight = gridX[context.column + 1];
				} while (gridRight < context.tx);
			} else if (context.tx < gridLeft) {
				// Line crosses at least 1 grid line on the left
				do {
					double intersectionY = by - ay * gridLeft;
					// Segments intersecting gridTop directly, before
					// reaching gridLeft.
					// t | |
					// \+---------+
					// (gridLeft, intersectionY) = \ |
					// |\ |
					// -+-\-------|-
					// | \ |
					// | \ |
					// gridTop = -+----\----+-
					// | \ |
					// | r |
					// gridTopSegments
					while (gridTop > intersectionY) {
						addSegment(context, bx - ax * gridTop, gridTop);
						// move up 1 grid.
						context.row = context.row - 1;
						gridTop = gridY[context.row];
					}
					// Segment intersecting gridLeft, starts at gridTop or
					// above if there are no gridTop segments.
					// t | |
					// \ | |
					// \-+---------+-
					// \| |
					// \ |
					// |\ |
					// | \ |
					// -+--\------+- = gridTop
					// | \ |
					// | r |
					// gridLeftSegment
					addSegment(context, gridLeft, intersectionY);
					// move 1 grid to the left.
					context.column = context.column - 1;
					gridLeft = gridX[context.column];
				} while (gridLeft > context.tx);
			}
			// Segments intersecting gridTop, but line ends before reaching
			// gridLeft or gridRight.
			// | t |
			// | \ |
			// -+---\-----+-
			// | \ |
			// | \ |
			// -+------\--|- = gridTop
			// | r |
			// | |
			// -+---------+-
			// | |
			// gridTopSegments
			while (gridTop > context.ty) {
				addSegment(context, bx - ax * gridTop, gridTop);
				// move up 1 grid.
				context.row = context.row - 1;
				gridTop = gridY[context.row];
			}
		}
		if (floorDistance > 0) {
			// final chunk
			addSegment(context, context.tx, context.ty);
			if (Math.round(actualDistance * 100) != Math
					.round(context.actualDistance * 100)) {
				log.error("segments", "Segments don't add up to: "
						+ actualDistance);
			}
			if (context.verbose) {
				logt.info("segments", "Cumulative distance: "
						+ context.actualDistance);
			}
		} else if (context.th > 1 && context.lossExponents != null) {
			// no coefficient, but still a loss
			double le = context.lossExponents[context.row][context.column];
			context.loss = le * Math.log10(context.th);
		}
	}

	private static void addSegment(SegmentsContext context, double endX,
			double endY) {
		double segmentFloorDistanceSquared = Math.pow(context.startX - endX, 2)
				+ Math.pow(context.startY - endY, 2);
		double segmentElevation = context.elevationRatio
				* Math.sqrt(segmentFloorDistanceSquared);
		double segmentActualDistance = Math.sqrt(segmentFloorDistanceSquared
				+ Math.pow(segmentElevation, 2));
		String message = null;
		if (segmentFloorDistanceSquared == 0) {
			// r must have been right on the edge of a grid
			if (context.verbose) {
				message = "Segment (" + context.startX + ", " + context.startY
						+ "), Zero length segment.";
			}
		} else {
			context.actualDistance += segmentActualDistance;
			if (context.verbose) {
				message = "Segment (" + context.startX + ", " + context.startY
						+ ") to (" + endX + ", " + endY + ")";
			}
			if (context.distanceToTransmitter > 1) {
				double Ak = context.distanceToTransmitter
						- segmentActualDistance;
				if (Ak < 1) {
					// Less than 1 meter left.
					// It means that either this is the last segment, or the
					// last segment is less than 1 meter and it must be included
					// in this segment.
					Ak = 1;
				}
				// add a coefficient for this grid
				if (context.A != null) {
					context.A[context.equationIndex][context.row
							* context.gridColumns + context.column] = Math
							.log10(context.distanceToTransmitter / Ak);
				}
				double le = context.lossExponents == null ? 0
						: context.lossExponents[context.row][context.column];
				context.loss = context.loss + le
						* Math.log10(context.distanceToTransmitter / Ak);
				if (context.verbose) {
					message += ", N(" + context.row + ", " + context.column
							+ ") = " + le + ",  Ak: " + Ak + ", loss: " + 10
							* context.loss;
				}
				context.distanceToTransmitter = Ak;
			}
			if (context.verbose) {
				logt.info("addSegment", message);
			}
			context.startX = endX;
			context.startY = endY;
		}
	}

	/*
	 * Fit
	 */
	private static class SegmentContext {
		double rx, ry, tx, ty, th, cosine, actualDistance, startX, startY, R1,
				R2, loss, u, v, maxRcond, maxSegmentError,
				maxSmallSegmentError, maxMediumSegmentError,
				tinySegmentDrr = 0.02, smallSegmentDrr = 0.2,
				mediumSegmentDrr = 0.5;

		double[] RA = new double[49], Rb = new double[7], h = new double[4],
				g = new double[4], b = new double[4], work = new double[4 * 7],
				gridX, gridY;

		double[][] A, B;

		doubleW rcond = new doubleW(-1);

		int[] p = new int[7];

		intW info = new intW(0);

		int xIndex, yIndex, equationIndex, segmentCount, smallSegmentCount,
				mediumSegmentCount, dgesvFailures;

		boolean verbose, verifyFit, measureIntegral;
	}

	/*
	 * Create grid of squares.
	 */
	public static double[] cgrd(double mapSize, double squareSize) {
		int gridLines = (int) (mapSize / squareSize) + 1;
		if ((gridLines - 1) * squareSize < mapSize) {
			gridLines++;
		}
		log.debug("createGrid", "# grid lines: " + gridLines);
		double[] grid = new double[gridLines + 5];
		grid[0] = -0.001;
		grid[1] = grid[0];
		grid[2] = grid[0];
		int gridIndex = 3; // 3 extra 0 elements
		while (grid[gridIndex] < mapSize) {
			gridIndex = gridIndex + 1;
			grid[gridIndex] = (gridIndex - 3) * squareSize;
		}
		grid[gridIndex] = mapSize + 0.001; // make sure that the right edge of
											// the map is inside the grid
		grid[gridIndex + 1] = grid[gridIndex];
		grid[gridIndex + 2] = grid[gridIndex]; // 2 extra elements at the end
		return grid;
	}

	private static int findGridIndex(double r, double[] gridLines) {
		int gridIndex = 3; // 3 extra 0 elements
		while (r >= gridLines[gridIndex]) {
			gridIndex = gridIndex + 1;
		}
		return gridIndex - 1;
	}

	public static boolean lli(double x1, double y1, double x2, double y2,
			WallLoss wall, Point2D ip) {
		double l1x1;
		double l1x2;
		if (x2 < x1) {
			l1x1 = x2;
			l1x2 = x1;
		} else {
			l1x1 = x1;
			l1x2 = x2;
		}
		double l1y1;
		double l1y2;
		if (y2 < y1) {
			l1y1 = y2;
			l1y2 = y1;
		} else {
			l1y1 = y1;
			l1y2 = y2;
		}
		double l2x1;
		double l2x2;
		if (wall.x2 < wall.x1) {
			l2x1 = wall.x2;
			l2x2 = wall.x1;
		} else {
			l2x1 = wall.x1;
			l2x2 = wall.x2;
		}
		double l2y1;
		double l2y2;
		if (wall.y2 < wall.y1) {
			l2y1 = wall.y2;
			l2y2 = wall.y1;
		} else {
			l2y1 = wall.y1;
			l2y2 = wall.y2;
		}
		llip(x1, y1, x2, y2, wall, ip);
		double ipx = ip.getX();
		double ipy = ip.getY();
		double tol = 1e-6;
		if (Math.abs(l1x1 - l1x2) < tol) {
			ipx = l1x1;
		} else if (Math.abs(l2x1 - l2x2) < tol) {
			ipx = l2x1;
		}
		if (Math.abs(l1y1 - l1y2) < tol) {
			ipy = l1y1;
		} else if (Math.abs(l2y1 - l2y2) < tol) {
			ipy = l2y1;
		}
		return ipx >= l1x1 && ipx <= l1x2 && ipx >= l2x1 && ipx <= l2x2
				&& ipy >= l1y1 && ipy <= l1y2 && ipy >= l2y1 && ipy <= l2y2;
	}

	public static void llip(double x1, double y1, double x2, double y2,
			WallLoss wall, Point2D ip) {
		// Line between l1p1 and l1p2
		double a1 = y2 - y1;
		double b1 = x1 - x2;
		double c1 = a1 * x1 + b1 * y1;

		// Line between l2p1 and l2p2
		double a2 = wall.y2 - wall.y1;
		double b2 = wall.x1 - wall.x2;
		double c2 = a2 * wall.x1 + b2 * wall.y1;

		// Intersection point
		double det = a1 * b2 - a2 * b1;
		double x = (b2 * c1 - b1 * c2) / det;
		double y = (a1 * c2 - a2 * c1) / det;

		ip.setLocation(x, y);
	}

	public static double lla(double x1, double y1, double x2, double y2,
			WallLoss wall) {
		double a1 = y1 - y2;
		double b1 = x1 - x2;
		double a2 = wall.y1 - wall.y2;
		double b2 = wall.x1 - wall.x2;

		double tan;
		if (b1 == 0) {
			tan = b2 / a2;
		} else if (b2 == 0) {
			tan = b1 / a1;
		} else {
			double m1 = a1 / b1;
			double m2 = a2 / b2;
			tan = (m2 - m1) / (1.0 + m1 * m2);
		}
		double angle = Math.atan(Math.abs(tan));
		if (angle > Math.PI / 2) {
			log.info_ln("% Angle should always be less than 90 degrees: "
					+ angle * 180.0 / Math.PI);
		}
		return angle;
	}

	public static double[][] clbrb(double[] apX, double[] apY,
			double[] apPower, short[] apChannel, double[] gridX,
			double[] gridY, int[][] neighborRSSI, double gamma, boolean verbose) {
		return clbrb_w(apX, apY, apPower, apChannel, gridX, gridY,
				neighborRSSI, gamma, null, verbose);
	}

	public static double[][] clbrb_w(double[] apX, double[] apY,
			double[] apPower, short[] apChannel, double[] gridX,
			double[] gridY, int[][] neighborRSSI, double gamma,
			WallLoss[] walls, boolean verbose) {
		SegmentContext context = new SegmentContext();
		context.verbose = false;
		context.B = null;
		context.gridX = gridX;
		context.gridY = gridY;
		int Mplus3 = gridX.length - 3;
		int Nplus3 = gridY.length - 3;
		int gridCount = Mplus3 * Nplus3;
		int numberOfMeasurements = apX.length * (apX.length - 1);
		int numberOfEquations = numberOfMeasurements + gridCount;
		context.A = new double[numberOfEquations][gridCount];
		double[] b = new double[numberOfEquations];
		context.equationIndex = 0;
		for (int i = 0; i < apX.length; i++) {
			context.rx = apX[i];
			context.ry = apY[i];
			int xIndex = findGridIndex(context.rx, gridX);
			int yIndex = findGridIndex(context.ry, gridY);
			for (int j = 0; j < apX.length; j++) {
				if (i == j) {
					continue;
				}
				if (neighborRSSI[i][j] == 0) {
					numberOfMeasurements--;
					continue;
				}
				context.xIndex = xIndex;
				context.yIndex = yIndex;
				context.tx = apX[j];
				context.ty = apY[j];
				context.th = 0; // assume all APs are at the same height
				segments(context, gridX, gridY);
				double wallLoss = getWallLoss(walls, context, false);
				// rssi = power - wl - (C + 10/log(10)*int_1^R)
				// or
				// (power - rssi - wl - C)*log(10)/10 = int_1^R
				log.info_non("RSSI (" + neighborRSSI[i][j] + ") = power("
						+ apPower[j] + ") - wall loss (" + wallLoss + ")");
                double C = apChannel[j] < 15 ? 20 * Math.log10(2400) - 28
                        : 20 * Math.log10(5500) - 28;
				double distance = Math.sqrt(Math
						.pow(context.rx - context.tx, 2)
						+ Math.pow(context.ry - context.ty, 2));
				double fsl = getPathLoss(distance, C, 20);
				int fsRssi = (int) (apPower[j] - fsl);
				int mwLoss = fsRssi - neighborRSSI[i][j];
				if (mwLoss < 0) {
					mwLoss = 0;
				}
				if (wallLoss > mwLoss) {
					wallLoss = mwLoss;
					log.info_non("Adjusting wall loss for calibration to: "
							+ wallLoss);
				}
                b[context.equationIndex] = (apPower[j] - (neighborRSSI[i][j] - 0.5)
						- wallLoss - C)
						* Math.log(10) / 10;
				context.equationIndex++;
			}
		}
		if (verbose) {
			lv(b, "b");
		}
		double sqrtGamma = Math.sqrt(gamma) / gridCount;
		double average = sqrtGamma * (gridCount - 1);
		for (int i = 0; i < gridCount; i++) {
			for (int j = 0; j < gridCount; j++) {
				if (i == j) {
					context.A[context.equationIndex][j] = average;
				} else {
					context.A[context.equationIndex][j] = -sqrtGamma;
				}
			}
			context.equationIndex++;
		}

		double[][] B = new double[Mplus3][Nplus3];
		Date start = new Date();
		boolean qr = true;
		if (qr) {
			if (!fitLossQR(context.A, b)) {
				return null;
			}
			for (int i = 0; i < Mplus3; i++) {
				for (int j = 0; j < Nplus3; j++) {
					B[i][j] = b[i + j * Mplus3];
				}
			}
		} else {
			int[] jpvt = fitLoss(context.A, b);
			if (jpvt == null) {
				return null;
			}
			for (int i = 0; i < gridCount; i++) {
				B[(jpvt[i] - 1) % Mplus3][(jpvt[i] - 1) / Mplus3] = b[i];
			}
		}
		Date end = new Date();
		long ms = end.getTime() - start.getTime();
		log.info_ln("% Overdetermined system with " + b.length
				+ " equations and " + gridCount + " variables solved in " + ms
				+ " ms.");
		log.info_ln("% " + context.smallSegmentCount
				+ " small segments out of " + context.segmentCount
				+ " found for calibration.");

		if (verbose) {
			lm(B, "B");
		}
		// Verify fit
		context.B = B;
		context.segmentCount = 0;
		context.smallSegmentCount = 0;
		context.maxRcond = 0;
		context.verbose = verbose;
		context.verifyFit = true;
		double cumulativeError = 0;
		for (int i = 0; i < apX.length; i++) {
			context.rx = apX[i];
			context.ry = apY[i];
			int xIndex = findGridIndex(context.rx, gridX);
			int yIndex = findGridIndex(context.ry, gridY);
			for (int j = 0; j < apX.length; j++) {
				if (i == j || neighborRSSI[i][j] == 0) {
					continue;
				}
				context.xIndex = xIndex;
				context.yIndex = yIndex;
				context.tx = apX[j];
				context.ty = apY[j];
				context.th = 0; // assume all APs are at the same height
                segments(context, gridX, gridY);
                double measuredRssi = neighborRSSI[i][j] - 0.5;
                double wallLoss = getWallLoss(walls, context, false);
                double C = apChannel[j] < 15 ? 20 * Math.log10(2400) - 28
                        : 20 * Math.log10(5500) - 28;
                double distance = Math.sqrt(Math
                        .pow(context.rx - context.tx, 2)
                        + Math.pow(context.ry - context.ty, 2));
                double fsl = getPathLoss(distance, C, 20);
                int fsRssi = (int) (apPower[j] - fsl);
                int mwLoss = fsRssi - neighborRSSI[i][j];
                if (mwLoss < 0) {
                    mwLoss = 0;
                }
                if (wallLoss > mwLoss) {
                    wallLoss = mwLoss;
                    log.info_non("Adjusting wall loss for calibration to: "
                            + wallLoss);
                }
                double estimatedRssi = apPower[j] - wallLoss
                        - (C + 10 * context.loss / Math.log(10));
                cumulativeError += Math.abs(estimatedRssi - measuredRssi);
                log.info_ln(String.format(
                        "%% Measured RSSI: %d.5, Fitted RSSI: %.4f, Diff: %.4f",
                        neighborRSSI[i][j], estimatedRssi, estimatedRssi
                                - measuredRssi));
			}
		}
		String fmt = "%% Diff between measured and fitted RSSI for %d measurements: %.4f, per measurement: %.4f";
		log.info_ln(String.format(fmt, numberOfMeasurements, cumulativeError,
				cumulativeError / numberOfMeasurements));
		fmt = "%% Matrix B is %dx%d and required calculating %d segments, %d small segments";
		log.info_ln(String.format(fmt, B.length, B[0].length,
				context.segmentCount, context.smallSegmentCount)
				+ ", max rcond: " + context.maxRcond);
		return B;
	}

	public static double wl(WallLoss[] walls, double x1, double y1, double x2,
			double y2, boolean verbose) {
		Point2D ip = new Point2D.Double();
		double wallLoss = 0;
		for (WallLoss wall : walls) {
			if (lli(x1, y1, x2, y2, wall, ip)) {
				double angle = lla(x1, y1, x2, y2, wall);
				double travel = wall.width / Math.sin(angle);
				double wl = travel * wall.absorption;
				if (verbose) {
					String fmt = "%% IP between (%.4f, %.4f) and (%.4f, %.4f) is at (%.4f, %.4f), angle: %.4f, loss: %.4f";
					log.info_ln(String.format(fmt, x1, y1, x2, y2, ip.getX(),
							ip.getY(), angle * 180.0 / Math.PI, wl));
					log.info_ln("Wall is between (" + wall.x1 + ", " + wall.y1
							+ " and (" + wall.x2 + ", " + wall.y2 + ")");
				}
				wallLoss += wl;
			}
		}
		return wallLoss;
	}

	private static double getWallLoss(WallLoss[] walls, SegmentContext context,
			boolean verbose) {
		if (walls == null) {
			return 0;
		}
		Point2D ip = new Point2D.Double();
		double wallLoss = 0;
		for (WallLoss wall : walls) {
			if (lli(context.rx, context.ry, context.tx, context.ty, wall, ip)) {
				double angle = lla(context.rx, context.ry, context.tx,
						context.ty, wall);
				double travel = wall.width / Math.sin(angle);
				double wl = travel * wall.absorption;
				double mwl = wall.absorption * wall.width * 1.7;
				if (wl > mwl) {
					wl = mwl;
				}
				if (verbose) {
					String fmt = "%% IP between (%.4f, %.4f) and (%.4f, %.4f) is at (%.4f, %.4f), angle: %.4f, loss: %.4f";
					log.info_ln(String.format(fmt, context.rx, context.ry,
							context.tx, context.ty, ip.getX(), ip.getY(), angle
									* 180.0 / Math.PI, wl));
				}
				wallLoss += wl;
			}
		}
		return wallLoss;
	}

	public static double[][] clbrb(double[] RX, double[] RY, double[] TX,
			double[] TY, double[] TH, double[] power, int[] frequency,
			int[] RSSI, double[] gridX, double[] gridY, double gamma,
			double ale, boolean verbose) {
		return clbrb_w(RX, RY, TX, TY, TH, power, frequency, RSSI, gridX,
				gridY, gamma, ale, null, verbose);
	}

	public static double[][] clbrb_w(double[] RX, double[] RY, double[] TX,
			double[] TY, double[] TH, double[] power, int[] frequency,
			int[] RSSI, double[] gridX, double[] gridY, double gamma,
			double ale, WallLoss[] walls, boolean verbose) {
		SegmentContext context = new SegmentContext();
		context.verbose = false;
		context.B = null;
		context.gridX = gridX;
		context.gridY = gridY;
		int Mplus3 = gridX.length - 3;
		int Nplus3 = gridY.length - 3;
		int gridCount = Mplus3 * Nplus3;
		int numberOfMeasurements = TX.length;
		int numberOfEquations = numberOfMeasurements + gridCount;
		context.A = new double[numberOfEquations][gridCount];
		double[] b = new double[numberOfEquations];
		context.equationIndex = 0;
		for (int i = 0; i < TX.length; i++) {
			context.rx = RX[i];
			context.ry = RY[i];
			context.xIndex = findGridIndex(context.rx, gridX);
			context.yIndex = findGridIndex(context.ry, gridY);
			context.tx = TX[i];
			context.ty = TY[i];
			context.th = TH[i];
			segments(context, gridX, gridY);
			double wallLoss = getWallLoss(walls, context, false);
			// rssi = power - wl - (C + 10/log(10)*int_1^R)
			// or
			// (power - rssi - wl - C)*log(10)/10 = int_1^R
			double C = 20 * Math.log10(frequency[i]) - 28;
			b[context.equationIndex] = (power[i] - RSSI[i] - C - wallLoss)
					* Math.log(10) / 10;
			context.equationIndex++;
		}
		double sqrtGamma = Math.sqrt(gamma) / gridCount;
		double average = sqrtGamma * (gridCount - 1);
		for (int i = 0; i < gridCount; i++) {
			for (int j = 0; j < gridCount; j++) {
				if (i == j) {
					context.A[context.equationIndex][j] = average;
				} else {
					context.A[context.equationIndex][j] = -sqrtGamma;
				}
			}
			context.equationIndex++;
		}

		double[][] B = new double[Mplus3][Nplus3];
		Date start = new Date();
		boolean qr = true;
		if (qr) {
			if (!fitLossQR(context.A, b)) {
				return null;
			}
			for (int i = 0; i < Mplus3; i++) {
				for (int j = 0; j < Nplus3; j++) {
					B[i][j] = b[i + j * Mplus3];
				}
			}
		} else {
			int[] jpvt = fitLoss(context.A, b);
			if (jpvt == null) {
				return null;
			}
			for (int i = 0; i < gridCount; i++) {
				B[(jpvt[i] - 1) % Mplus3][(jpvt[i] - 1) / Mplus3] = b[i];
			}
		}
		Date end = new Date();
		long ms = end.getTime() - start.getTime();
		log.info_ln("% Overdetermined system with " + b.length
				+ " equations and " + gridCount + " variables solved in " + ms
				+ " ms.");
		log.info_ln("% " + context.smallSegmentCount
				+ " small segments out of " + context.segmentCount
				+ " found for calibration.");

		// Verify fit
		context.B = B;
		context.segmentCount = 0;
		context.smallSegmentCount = 0;
		context.maxRcond = 0;
		context.verbose = verbose;
		context.verifyFit = true;
		double cumulativeError = 0;
		double cumulativeAveragedError = 0;
		for (int i = 0; i < TX.length; i++) {
			context.rx = RX[i];
			context.ry = RY[i];
			context.xIndex = findGridIndex(context.rx, gridX);
			context.yIndex = findGridIndex(context.ry, gridY);
			context.tx = TX[i];
			context.ty = TY[i];
			context.th = TH[i];
			segments(context, gridX, gridY);
			double measuredRssi = RSSI[i];
			double C = 20 * Math.log10(frequency[i]) - 28;
			double wallLoss = getWallLoss(walls, context, false);
			double estimatedRssi = power[i] - wallLoss
					- (C + 10 * context.loss / Math.log(10));
			cumulativeError += Math.abs(estimatedRssi - measuredRssi);
			double floorDistance = Math.pow(context.rx - context.tx, 2)
					+ Math.pow(context.ry - context.ty, 2);
			double actualDistance = Math.sqrt(floorDistance
					+ Math.pow(context.th, 2));
			double averagedRssi = power[i]
					- (C + 10 * ale * Math.log10(actualDistance));
			cumulativeAveragedError += Math.abs(averagedRssi - measuredRssi);
			String fmt = "%% Measured RSSI: %d, Averaged RSSI: %.4f, Fitted RSSI: %.4f, Diff: %.4f";
			log.info_ln(String.format(fmt, RSSI[i], averagedRssi,
					estimatedRssi, estimatedRssi - measuredRssi));
		}
		String fmt = "%% Diff between measured and fitted RSSI for %d measurements: %.4f, per measurement: %.4f";
		log.info_ln(String.format(fmt, numberOfMeasurements, cumulativeError,
				cumulativeError / numberOfMeasurements));
		fmt = "%% Diff between measured and averaged RSSI for %d measurements: %.4f, per measurement: %.4f";
		log.info_ln(String.format(fmt, numberOfMeasurements,
				cumulativeAveragedError, cumulativeAveragedError
						/ numberOfMeasurements));
		fmt = "%% Matrix B is %dx%d and required calculating %d segments, %d small segments";
		log.info_ln(String.format(fmt, B.length, B[0].length,
				context.segmentCount, context.smallSegmentCount)
				+ ", max rcond: " + context.maxRcond);
		return B;
	}

	private static void segments(SegmentContext context, double[] gridX,
			double[] gridY) {
		double floorDistance = Math.pow(context.rx - context.tx, 2)
				+ Math.pow(context.ry - context.ty, 2);
		double actualDistance = Math.sqrt(floorDistance
				+ Math.pow(context.th, 2));
		floorDistance = Math.sqrt(floorDistance);
		double floorDistance1meter = floorDistance / actualDistance;
		if (context.verbose) {
			log.info_ln("% r("
					+ String.format("%.4f, %.4f), t(%.4f, %.4f, %.4f)",
							context.rx, context.ry, context.tx, context.ty,
							context.th)
					+ String.format(
							", floor distance: %.4f, actual distance: %.4f",
							floorDistance, actualDistance));
		}
		context.startX = context.rx;
		context.startY = context.ry;
		context.R2 = actualDistance;
		context.actualDistance = 0;
		context.cosine = context.th / floorDistance;
		context.loss = 0;

		double dx = context.rx - context.tx;
		double dy = context.ry - context.ty;
		context.u = dx / floorDistance * floorDistance1meter;
		context.v = dy / floorDistance * floorDistance1meter;
		double ax = (context.tx - context.rx) / dy;
		double bx = ax * context.ry + context.rx;
		double ay = (context.ty - context.ry) / dx;
		double by = ay * context.rx + context.ry;
		double gridLeft = gridX[context.xIndex];
		double gridRight = gridX[context.xIndex + 1];
		double gridTop = gridY[context.yIndex];
		double gridBottom = gridY[context.yIndex + 1];

		if (context.ty > context.ry) {
			// Line goes down
			if (context.tx > gridRight) {
				// Line crosses at least 1 grid line on the right
				do {
					double intersectionY = by - ay * gridRight;
					// Segments intersecting gridBottom directly, before
					// reaching gridRight.
					// | r |
					// | \ |
					// -+----\----+- = gridBottom
					// | \ |
					// | \ |
					// -+-------\-|-
					// | \|
					// | \ = (gridRight, intersectionY)
					// -+---------+\
					// | | t
					// gridBottomSegments
					while (gridBottom < intersectionY) {
						// Crossing gridBottom
						addSegment(context, bx - ax * gridBottom, gridBottom);
						// move down 1 grid.
						gridBottom = gridY[++context.yIndex + 1];
					}
					// Segment intersecting gridRight, starts at gridBottom or
					// below if there are no gridBottom segments.
					// | r |
					// | \ |
					// -+------\--+- = gridBottom
					// | \ |
					// | \|
					// | \
					// | |\
					// -+---------+ \
					// | | \
					// | | t
					// gridRightSegment
					addSegment(context, gridRight, intersectionY);
					// move 1 grid to the right.
					gridRight = gridX[++context.xIndex + 1];
				} while (gridRight < context.tx);
			} else if (context.tx < gridLeft) {
				// Line crosses at least 1 grid line on the left
				do {
					double intersectionY = by - ay * gridLeft;
					// Segments intersecting gridBottom directly, before
					// reaching gridLeft.
					// | r |
					// | / |
					// gridBottom = -+----/----+-
					// | / |
					// | / |
					// -+-/-------|-
					// |/ |
					// (gridLeft, intersectionY) = / |
					// /+---------+
					// t | |
					// gridBottomSegments
					while (gridBottom < intersectionY) {
						// Crossing gridBottom
						addSegment(context, bx - ax * gridBottom, gridBottom);
						// move down 1 grid.
						gridBottom = gridY[++context.yIndex + 1];
					}
					// Segment intersecting gridLeft, starts at gridBottom or
					// below if there are no gridBottom segments.
					// | r |
					// | / |
					// -+--/------+- = gridBottom
					// | / |
					// |/ |
					// / |
					// /| |
					// /-+---------+-
					// / | |
					// t | | t
					// gridLeftSegment
					addSegment(context, gridLeft, intersectionY);
					// move 1 grid to the left.
					gridLeft = gridX[--context.xIndex];
				} while (gridLeft > context.tx);
			}
			// Segments intersecting gridBottom, but line ends before reaching
			// gridLeft or gridRight.
			// | r |
			// | \ |
			// -+---\-----+- = gridBottom
			// | \ |
			// | \ |
			// -+------\--|-
			// | t |
			// | |
			// -+---------+-
			// | |
			// gridBottomSegments
			while (gridBottom < context.ty) {
				// Crossing gridBottom
				addSegment(context, bx - ax * gridBottom, gridBottom);
				// move down 1 grid.
				gridBottom = gridY[++context.yIndex + 1];
			}
		} else {
			// Line goes up or horizontal.
			if (context.tx > gridRight) {
				// Line crosses at least 1 grid line on the right
				do {
					double intersectionY = by - ay * gridRight;
					// Segments intersecting gridTop directly, before
					// reaching gridRight.
					// | | t
					// -+---------+/
					// | / = (gridRight, intersectionY)
					// | /|
					// -+-------/-|-
					// | / |
					// | / |
					// -+----/----+- = gridTop
					// | / |
					// | r |
					// gridTopSegments
					while (gridTop > intersectionY) {
						addSegment(context, bx - ax * gridTop, gridTop);
						// move up 1 grid.
						gridTop = gridY[--context.yIndex];
					}
					// Segment intersecting gridRight, starts at gridTop or
					// above if there are no gridTop segments.
					// | | t
					// | | /
					// -+---------+ /
					// | |/
					// | /
					// | /|
					// | / |
					// -+------/--+- = gridTop
					// | / |
					// | r |
					// gridRightSegment
					addSegment(context, gridRight, intersectionY);
					// move 1 grid to the right.
					gridRight = gridX[++context.xIndex + 1];
				} while (gridRight < context.tx);
			} else if (context.tx < gridLeft) {
				// Line crosses at least 1 grid line on the left
				do {
					double intersectionY = by - ay * gridLeft;
					// Segments intersecting gridTop directly, before
					// reaching gridLeft.
					// t | |
					// \+---------+
					// (gridLeft, intersectionY) = \ |
					// |\ |
					// -+-\-------|-
					// | \ |
					// | \ |
					// gridTop = -+----\----+-
					// | \ |
					// | r |
					// gridTopSegments
					while (gridTop > intersectionY) {
						addSegment(context, bx - ax * gridTop, gridTop);
						// move up 1 grid.
						gridTop = gridY[--context.yIndex];
					}
					// Segment intersecting gridLeft, starts at gridTop or
					// above if there are no gridTop segments.
					// t | |
					// \ | |
					// \-+---------+-
					// \| |
					// \ |
					// |\ |
					// | \ |
					// -+--\------+- = gridTop
					// | \ |
					// | r |
					// gridLeftSegment
					addSegment(context, gridLeft, intersectionY);
					// move 1 grid to the left.
					gridLeft = gridX[--context.xIndex];
				} while (gridLeft > context.tx);
			}
			// Segments intersecting gridTop, but line ends before reaching
			// gridLeft or gridRight.
			// | t |
			// | \ |
			// -+---\-----+-
			// | \ |
			// | \ |
			// -+------\--|- = gridTop
			// | r |
			// | |
			// -+---------+-
			// | |
			// gridTopSegments
			while (gridTop > context.ty) {
				addSegment(context, bx - ax * gridTop, gridTop);
				// move up 1 grid.
				gridTop = gridY[--context.yIndex];
			}
		}
		if (floorDistance > 0) {
			// final chunk
			addSegment(context, context.tx, context.ty);
			if (Math.round(actualDistance * 100) != Math
					.round(context.actualDistance * 100)) {
				log.error("% Segments don't add up to: " + actualDistance);
			}
			if (context.verbose) {
				log.info_ln(String.format("%% Cumulative distance: %.4f\n",
						context.actualDistance));
			}
		} else if (context.th > 1 && context.B != null) {
			// no coefficient, but still a loss
			log.info_ln("%% r just below AP");
			context.loss = Math.log(10) * le(context, context.tx, context.ty)
					* Math.log10(context.th);
		}
	}

	private static void addSegment(SegmentContext context, double endX,
			double endY) {
		String message = null;
		if (context.verbose) {
			message = String.format("%% Segment (%.4f, %.4f) to (%.4f, %.4f)",
					context.startX, context.startY, endX, endY);
		}
		if (context.R2 > 1) {
			double R1FloorDistanceSquared = Math.pow(context.tx - endX, 2)
					+ Math.pow(context.ty - endY, 2);
			double R1Elevation = context.cosine
					* Math.sqrt(R1FloorDistanceSquared);
			context.R1 = Math.sqrt(R1FloorDistanceSquared
					+ Math.pow(R1Elevation, 2));
			context.actualDistance += context.R2 - context.R1;
			if (context.R1 < 1) {
				// Less than 1 meter left.
				context.actualDistance += context.R1;
				// It means that either this is the last segment, or the
				// last segment is less than 1 meter and it must be included
				// in this segment.
				context.R1 = 1;
			}
			if (context.R2 - context.R1 < 1e-10) {
				// r must have been right on the edge of a square
				if (context.verbose) {
					message += ", Zero length segment.";
				}
			} else {
				context.segmentCount++;
				double drr = (context.R2 - context.R1) / context.R1;
				if (context.B == null) {
					// add equation
					integralw(context, drr < context.tinySegmentDrr);
				} else {
					// calculate loss over this segment
					context.loss += segmentLoss(context, drr);
				}
				if (context.verbose) {
					message += String.format(
							", R1: %.4f, R2: %.4f, length: %.4f, loss: %.4f",
							context.R1, context.R2, context.R2 - context.R1, 10
									* context.loss / Math.log(10));
				}
				context.R2 = context.R1;
			}
			context.startX = endX;
			context.startY = endY;
		} else if (context.actualDistance == 0) {
			context.actualDistance = context.R2;
		}
		if (context.verbose) {
			log.info_ln(message);
		}
	}

	private static double segmentLoss(SegmentContext context, double drr) {
		double segmentLoss;
		if (context.verifyFit) {
			// To verify fit, calculate exact integrals
			if (drr < context.tinySegmentDrr) {
				context.smallSegmentCount++;
				integral6d(context, true); // Just to find out condition number
				segmentLoss = integral2d(context);
			} else {
				// exact integral
				segmentLoss = integral6d(context, false);
			}
		} else {
			if (drr < context.smallSegmentDrr) {
				// More flexible regarding small segment
				context.smallSegmentCount++;
				segmentLoss = integral2d(context);
			} else if (drr < context.mediumSegmentDrr) {
				context.mediumSegmentCount++;
				segmentLoss = integral3d(context);
			} else {
				segmentLoss = integral4d(context);
			}
			if (context.measureIntegral) {
				measureIntegral(context, drr, segmentLoss);
			}
		}
		return segmentLoss;
	}

	private static void measureIntegral(SegmentContext context, double drr,
			double segmentLoss) {
		if (drr < context.tinySegmentDrr) {
			// No error for tiny segments
			return;
		}
		double exactLoss = integral6d(context, false);
		double segmentError = Math.abs(exactLoss - segmentLoss) / exactLoss;
		if (drr < context.smallSegmentDrr) {
			if (context.maxSmallSegmentError < segmentError) {
				context.maxSmallSegmentError = segmentError;
			}
		} else if (drr < context.mediumSegmentDrr) {
			if (context.maxMediumSegmentError < segmentError) {
				context.maxMediumSegmentError = segmentError;
			}
		} else {
			if (context.maxSegmentError < segmentError) {
				context.maxSegmentError = segmentError;
			}
		}
		if (segmentError > 0.05) {
			logt.info("measureIntegral", "Integral error is " + segmentError
					+ " for segment (" + context.R1 + ", " + context.R2 + ") "
					+ (context.R2 - context.R1) / context.R1);
		}
	}

	private static void segmentEquation(SegmentContext context, double rk,
			double rkx, double rky, double weight) {
		gx(rkx, context.gridX, context.xIndex, context.g);
		gx(rky, context.gridY, context.yIndex, context.h);
		for (int yi = context.yIndex - 3; yi <= context.yIndex; yi++) {
			for (int xi = context.xIndex - 3; xi <= context.xIndex; xi++) {
				context.A[context.equationIndex][yi
						* (context.gridX.length - 3) + xi] += weight
						* context.h[yi - context.yIndex + 3]
						* context.g[xi - context.xIndex + 3] / rk;
			}
		}
	}

	private static void integralw(SegmentContext context, boolean shortSegment) {
		int n;
		if (shortSegment) {
			context.smallSegmentCount++;
			log.debug("integralw", "Small segment R1: " + context.R1 + ", R2: "
					+ context.R2);
			context.RA[0] = context.R2;
			context.RA[1] = (context.R1 + context.R2) / 2;
			context.RA[2] = context.R1;
			double f = 4 / (context.R1 + context.R2);
			double w0 = (2 * Math.log(context.R2 / context.R1)
					/ (context.R2 - context.R1) - f)
					/ (1 / context.R2 - f + 1 / context.R1);
			f = (context.R2 - context.R1) / 2;
			context.Rb[0] = w0 * f;
			context.Rb[1] = (2 - 2 * w0) * f;
			context.Rb[2] = context.Rb[0];
			n = 3;
		} else {
			weights6d(context);
			if (!poly6d(context)) {
				return;
			}
			rk6d(context);
			n = 7;
		}
		double segmentLoss = 0;
		for (int i = 0; i < n; i++) {
			double rk = context.RA[i];
			double rkx = context.tx + rk * context.u;
			double rky = context.ty + rk * context.v;
			if (context.B == null) {
				segmentEquation(context, rk, rkx, rky, context.Rb[i]);
			} else {
				segmentLoss += context.Rb[i] * le_b(context, rkx, rky) / rk;
			}
		}
		context.loss += segmentLoss;
	}

	private static final double weights6d[] = { 7, 1, 1, 1, 1, 1, 1, 6, 1,
			Math.sqrt(3) / 2, 1.0 / 2, 0, -1.0 / 2, -Math.sqrt(3) / 2, 5, 1,
			1.0 / 2, -1.0 / 2, -1, -1.0 / 2, 1.0 / 2, 4, 1, 0, -1, 0, 1, 0, 3,
			1, -1.0 / 2, -1.0 / 2, 1, -1.0 / 2, -1.0 / 2, 2, 1,
			-Math.sqrt(3) / 2, 1.0 / 2, 0, -1.0 / 2, Math.sqrt(3) / 2, 1, 1,
			-1, 1, -1, 1, -1 };

	private static void weights6d(SegmentContext context) {
		for (int i = 0; i < weights6d.length; i++) {
			context.RA[i] = weights6d[i];
		}
		double a = 2 - Math.sqrt(3);
		double b = 2 + Math.sqrt(3);
		context.RA[0] = 1 / context.R2;
		context.RA[7] = 4 / (a * context.R1 + b * context.R2);
		context.RA[14] = 4 / (context.R1 + 3 * context.R2);
		context.RA[21] = 2 / (context.R1 + context.R2);
		context.RA[28] = 4 / (3 * context.R1 + context.R2);
		context.RA[35] = 4 / (b * context.R1 + a * context.R2);
		context.RA[42] = 1 / context.R1;

		// Avoids multiplying by 2 / dr and then after solving the
		// equations again multiply by dr / 2
		double dr = context.R2 - context.R1;
		context.Rb[0] = Math.log(context.R2 / context.R1);
		context.Rb[1] = dr;
		context.Rb[2] = 0;
		context.Rb[3] = -dr / 3;
		context.Rb[4] = 0;
		context.Rb[5] = -dr / 15;
		context.Rb[6] = 0;
	}

	public static final double integral6d[] = { 1, 1, 1, 1, 1, 1, 1, 1,
			Math.sqrt(3) / 2, 1.0 / 2, 0, -1.0 / 2, -Math.sqrt(3) / 2, -1, 1,
			1.0 / 2, -1.0 / 2, -1, -1.0 / 2, 1.0 / 2, 1, 1, 0, -1, 0, 1, 0, -1,
			1, -1.0 / 2, -1.0 / 2, 1, -1.0 / 2, -1.0 / 2, 1, 1,
			-Math.sqrt(3) / 2, 1.0 / 2, 0, -1.0 / 2, Math.sqrt(3) / 2, -1 };

	private static double integral6d(SegmentContext context, boolean rcond) {
		for (int i = 0; i < integral6d.length; i++) {
			context.RA[i + 7] = integral6d[i];
		}
		rk6d(context);

		for (int i = 0; i < 7; i++) {
			double rk = context.RA[i];
			double rkx = context.tx + rk * context.u;
			double rky = context.ty + rk * context.v;
			context.Rb[i] = le_b(context, rkx, rky) / rk;
		}
		for (int i = 0; i < 7; i++) {
			context.RA[i] = 1 / context.RA[i];
		}

		if (rcond) {
			poly6dc(context);
			return 0;
		} else if (!poly6d(context)) {
			return 0;
		}

		double loss = context.Rb[0] * Math.log(context.R2 / context.R1)
				+ (context.R2 - context.R1)
				* (context.Rb[1] - context.Rb[3] / 3 - context.Rb[5] / 15);
		if (loss < 0) {
			log.info_ln("Loss for this segment is negative, probably small: "
					+ 10 * loss / Math.log(10));
			loss = 0;
		}
		return loss;
	}

	private static boolean poly6d(SegmentContext context) {
		Dgesv.dgesv(7, 1, context.RA, 0, 7, context.p, 0, context.Rb, 0, 7,
				context.info);
		if (context.info.val != 0) {
			log.debug("poly6d", "DGESV failed (" + context.info.val
					+ ") with Chebyshev polynomials for R1: " + context.R1
					+ ", R2: " + context.R2 + ", length: "
					+ (context.R2 - context.R1));
			context.dgesvFailures++;
			return false;
		}
		return true;
	}

	private static void poly6dc(SegmentContext context) {
		double norm = Dlange.dlange("1", 7, 7, context.RA, 0, 7, null, 0);
		if (!poly6d(context)) {
			return;
		}
		Dgecon.dgecon("1", 7, context.RA, 0, 7, norm, context.rcond,
				context.work, 0, context.p, 0, context.info);
		if (context.info.val != 0) {
			logt.info("poly6dc", "DGECON failed.");
		} else {
			if (context.rcond.val > context.maxRcond) {
				context.maxRcond = context.rcond.val;
			}
		}
	}

	private static void rk6d(SegmentContext context) {
		double a = 2 - Math.sqrt(3);
		double b = 2 + Math.sqrt(3);
		context.RA[0] = context.R2;
		context.RA[1] = (a * context.R1 + b * context.R2) / 4;
		context.RA[2] = (context.R1 + 3 * context.R2) / 4;
		context.RA[3] = (context.R1 + context.R2) / 2;
		context.RA[4] = (3 * context.R1 + context.R2) / 4;
		context.RA[5] = (b * context.R1 + a * context.R2) / 4;
		context.RA[6] = context.R1;
	}

	public static final double integral4d[] = { 1, 1, 1, 1, 1, 1,
			Math.sqrt(2) / 2, 0, -Math.sqrt(2) / 2, -1, 1, 0, -1, 0, 1, 1,
			-Math.sqrt(2) / 2, 0, Math.sqrt(2) / 2, -1 };

	private static double integral4dm(SegmentContext context) {
		for (int i = 0; i < integral4d.length; i++) {
			context.RA[i + 5] = integral4d[i];
		}
		double s2 = Math.sqrt(2);
		double c1 = 4 / ((2 - s2) * context.R1 + (2 + s2) * context.R2);
		double c2 = 2 / (context.R1 + context.R2);
		double c3 = 4 / ((2 + s2) * context.R1 + (2 - s2) * context.R2);
		context.RA[0] = 1 / context.R2;
		context.RA[1] = c1;
		context.RA[2] = c2;
		context.RA[3] = c3;
		context.RA[4] = 1 / context.R1;

		double rk = context.R2;
		double rkx = context.tx + rk * context.u;
		double rky = context.ty + rk * context.v;
		context.Rb[0] = le_b(context, rkx, rky) / rk;
		rk = 1 / c1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[1] = le_b(context, rkx, rky) / rk;
		rk = 1 / c2;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[2] = le_b(context, rkx, rky) / rk;
		rk = 1 / c3;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[3] = le_b(context, rkx, rky) / rk;
		rk = context.R1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[4] = le_b(context, rkx, rky) / rk;

		Dgesv.dgesv(5, 1, context.RA, 0, 5, context.p, 0, context.Rb, 0, 5,
				context.info);
		if (context.info.val != 0) {
			logt.info("integral4dm", "DGESV failed (" + context.info.val
					+ ") with Chebyshev polynomials for R1: " + context.R1
					+ ", R2: " + context.R2 + ", (R2-R1)/R1: "
					+ (context.R2 - context.R1) / context.R1);
			return 0;
		}
		double loss = context.Rb[0] * Math.log(context.R2 / context.R1)
				+ (context.R2 - context.R1)
				* (context.Rb[1] - context.Rb[3] / 3);
		return loss;
	}

	private static double integral4d(SegmentContext context) {
		double s2 = Math.sqrt(2);
		double c1 = 4 / ((2 - s2) * context.R1 + (2 + s2) * context.R2);
		double c2 = 2 / (context.R1 + context.R2);
		double c3 = 4 / ((2 + s2) * context.R1 + (2 - s2) * context.R2);

		double rk = context.R2;
		double rkx = context.tx + rk * context.u;
		double rky = context.ty + rk * context.v;
		double f0 = le_b(context, rkx, rky) / rk;
		rk = 1 / c1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f1 = le_b(context, rkx, rky) / rk;
		rk = 1 / c2;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f2 = le_b(context, rkx, rky) / rk;
		rk = 1 / c3;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f3 = le_b(context, rkx, rky) / rk;
		rk = context.R1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f4 = le_b(context, rkx, rky) / rk;

		double a0 = (f0 + f4 + 2 * (f2 - f1 - f3))
				/ (1 / context.R2 + 1 / context.R1 + 2 * (c2 - c1 - c3));
		double a1 = (f1 + f3 - (c1 + c3) * a0) / 2;
		double a3 = c2 * a0 + a1 - f2;
		// No need for a2 & a4
		// context.Rb[0] = a0;
		// context.Rb[1] = a1;
		// context.Rb[2] = (f1 - c1 * a0 - a1) / s2 + (f0 - f4 - (c0 - c4) * a0)
		// / 4;
		// context.Rb[3] = a3;
		// context.Rb[4] = (f0 - f4 - (c0 - c4) * a0) / 2 - context.Rb[2];

		double loss = a0 * Math.log(context.R2 / context.R1)
				+ (context.R2 - context.R1) * (a1 - a3 / 3);
		return loss;
	}

	private static double integral3dm(SegmentContext context) {
		double c1 = 4 / (context.R1 + 3 * context.R2);
		double c2 = 4 / (3 * context.R1 + context.R2);
		context.RA[0] = 1 / context.R2;
		context.RA[1] = c1;
		context.RA[2] = c2;
		context.RA[3] = 1 / context.R1;

		context.RA[4] = 1;
		context.RA[5] = 1;
		context.RA[6] = 1;
		context.RA[7] = 1;

		context.RA[8] = 1;
		context.RA[9] = 0.5;
		context.RA[10] = -0.5;
		context.RA[11] = -1;

		context.RA[12] = 1;
		context.RA[13] = -0.5;
		context.RA[14] = -0.5;
		context.RA[15] = 1;

		double rk = context.R2;
		double rkx = context.tx + rk * context.u;
		double rky = context.ty + rk * context.v;
		context.Rb[0] = le_b(context, rkx, rky) / rk;
		rk = 1 / c1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[1] = le_b(context, rkx, rky) / rk;
		rk = 1 / c2;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[2] = le_b(context, rkx, rky) / rk;
		rk = context.R1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[3] = le_b(context, rkx, rky) / rk;

		Dgesv.dgesv(4, 1, context.RA, 0, 4, context.p, 0, context.Rb, 0, 4,
				context.info);
		if (context.info.val != 0) {
			logt.info("integral3dm", "DGESV failed (" + context.info.val
					+ ") with Chebyshev polynomials for R1: " + context.R1
					+ ", R2: " + context.R2 + ", (R2-R1)/R1: "
					+ (context.R2 - context.R1) / context.R1);
			return 0;
		}
		double loss = context.Rb[0] * Math.log(context.R2 / context.R1)
				+ (context.R2 - context.R1)
				* (context.Rb[1] - context.Rb[3] / 3);
		return loss;
	}

	private static double integral3d(SegmentContext context) {
		double c0 = 1 / context.R2;
		double c1 = 4 / (context.R1 + 3 * context.R2);
		double c2 = 4 / (3 * context.R1 + context.R2);
		double c3 = 1 / context.R1;
		double rkx = context.tx + context.R2 * context.u;
		double rky = context.ty + context.R2 * context.v;
		double f0 = le_b(context, rkx, rky) / context.R2;
		double rk = 1 / c1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f1 = le_b(context, rkx, rky) / rk;
		rk = 1 / c2;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f2 = le_b(context, rkx, rky) / rk;
		rkx = context.tx + context.R1 * context.u;
		rky = context.ty + context.R1 * context.v;
		double f3 = le_b(context, rkx, rky) / context.R1;
		double c12 = c1 - c2;
		double f12 = f1 - f2;
		double a0 = (f0 - f3 - 2 * f12) / (c0 - c3 - 2 * c12);
		double a2 = f12 - c12 * a0;
		double a3 = 2 * (f0 - f1 - (c0 - c1) * a0 - a2 / 2) / 3;
		double a1 = (f0 + f3 - (c0 + c3) * a0) / 2 - a3;
		double loss = a0 * Math.log(context.R2 / context.R1)
				+ (context.R2 - context.R1) * (a1 - a3 / 3);
		return loss;
	}

	private static double integral2dm(SegmentContext context) {
		context.RA[0] = 1 / context.R2;
		context.RA[1] = 2 / (context.R1 + context.R2);
		context.RA[2] = 1 / context.R1;
		context.RA[3] = 1;
		context.RA[4] = 1;
		context.RA[5] = 1;
		context.RA[6] = 1;
		context.RA[7] = 0;
		context.RA[8] = -1;

		double rk = context.R2;
		double rkx = context.tx + rk * context.u;
		double rky = context.ty + rk * context.v;
		context.Rb[0] = le_b(context, rkx, rky) / rk;
		rk = (context.R1 + context.R2) / 2;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[1] = le_b(context, rkx, rky) / rk;
		rk = context.R1;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		context.Rb[2] = le_b(context, rkx, rky) / rk;

		Dgesv.dgesv(3, 1, context.RA, 0, 3, context.p, 0, context.Rb, 0, 3,
				context.info);
		if (context.info.val != 0) {
			logt.info("integral2dm", "DGESV failed (" + context.info.val
					+ ") with Chebyshev polynomials for R1: " + context.R1
					+ ", R2: " + context.R2 + ", length: "
					+ (context.R2 - context.R1));
			return 0;
		}
		double loss = context.Rb[0] * Math.log(context.R2 / context.R1)
				+ (context.R2 - context.R1) * context.Rb[1];
		return loss;
	}

	private static double integral2d(SegmentContext context) {
		double rkx = context.tx + context.R2 * context.u;
		double rky = context.ty + context.R2 * context.v;
		double f0 = le_b(context, rkx, rky) / context.R2;
		double rk = (context.R1 + context.R2) / 2;
		rkx = context.tx + rk * context.u;
		rky = context.ty + rk * context.v;
		double f1 = le_b(context, rkx, rky) / rk;
		rkx = context.tx + context.R1 * context.u;
		rky = context.ty + context.R1 * context.v;
		double f2 = le_b(context, rkx, rky) / context.R1;
		double a0 = (f0 - 2 * f1 + f2)
				/ (1 / context.R1 + 1 / context.R2 - 2 / rk);
		double loss = a0 * Math.log(context.R2 / context.R1) + (f1 - a0 / rk)
				* (context.R2 - context.R1);
		return loss;
	}

	private static double integral1d(SegmentContext context) {
		double rkx = context.tx + context.R2 * context.u;
		double rky = context.ty + context.R2 * context.v;
		double f0 = le_b(context, rkx, rky) / context.R2;
		rkx = context.tx + context.R1 * context.u;
		rky = context.ty + context.R1 * context.v;
		double f1 = le_b(context, rkx, rky) / context.R1;
		double a0 = (f0 - f1) / (1 / context.R2 - 1 / context.R1);
		double loss = a0 * Math.log(context.R2 / context.R1)
				+ (f1 - a0 / context.R1) * (context.R2 - context.R1);
		return loss;
	}

	private static double le(SegmentContext context, double x, double y) {
		gx(x, context.gridX, context.xIndex, context.g);
		gx(y, context.gridY, context.yIndex, context.h);
		double le = 0;
		for (int yi = context.yIndex - 3; yi <= context.yIndex; yi++) {
			double gtB = 0;
			for (int xi = context.xIndex - 3; xi <= context.xIndex; xi++) {
				gtB = gtB + context.g[xi - context.xIndex + 3]
						* context.B[xi][yi];
			}
			le += gtB * context.h[yi - context.yIndex + 3];
		}
		return le;
	}

	private static void gx(double x, double[] alpha, int alphaIndex, double[] gx) {
		gx[3] = 1;
		for (int k = 1; k <= 3; k++) {
			double f = 0;
			int j = 4 - k;
			for (int i = alphaIndex - k; i <= alphaIndex - 1; i++) {
				// at this point f is either 0 or gx(j)/(alpha(i+k)-alpha(i))
				// because in the previous iteration it was set to
				// gx(j+1)/(alpha(i+k+1)-alpha(i+1))
				double t = (x - alpha[i]) * f;
				// by unrolling last iteration, alpha(i+k+1)-alpha(i+1) should
				// never be 0.
				f = gx[j] / (alpha[i + k + 1] - alpha[i + 1]);
				gx[j - 1] = t - (x - alpha[i + k + 1]) * f;
				j = j + 1;
			}
			// unroll last iteration
			gx[3] = (x - alpha[alphaIndex]) * f;
		}
	}

	private static double le_b(SegmentContext context, double x, double y) {
		double[] h = g_right(y, context.gridY, context.yIndex);
		for (int xi = context.xIndex - 3; xi <= context.xIndex; xi++) {
			double Bh = 0;
			for (int yi = context.yIndex - 3; yi <= context.yIndex; yi++) {
				Bh = Bh + h[yi - context.yIndex + 3] * context.B[xi][yi];
			}
			context.b[xi - context.xIndex + 3] = Bh;
		}
		return g_left(x, context.gridX, context.xIndex, context.b);
	}

	private static double g_left(double x, double[] alpha, int i, double[] b) {
		double x1 = x - alpha[i];
		double x2 = x - alpha[i - 1];
		double wi_1 = x1 / (alpha[i + 1] - alpha[i]);
		double wi_2 = x1 / (alpha[i + 2] - alpha[i]);
		double wi1_2 = x2 / (alpha[i + 1] - alpha[i - 1]);
		double wi_3 = x1 / (alpha[i + 3] - alpha[i]);
		double wi1_3 = x2 / (alpha[i + 2] - alpha[i - 1]);
		double wi2_3 = (x - alpha[i - 2]) / (alpha[i + 1] - alpha[i - 2]);
		double w32 = (1 - wi1_3) * b[1] + wi1_3 * b[2];
		return (1 - wi_1)
				* ((1 - wi1_2) * ((1 - wi2_3) * b[0] + wi2_3 * b[1]) + wi1_2
						* w32) + wi_1
				* ((1 - wi_2) * w32 + wi_2 * ((1 - wi_3) * b[2] + wi_3 * b[3]));
	}

	private static double[] g_right(double x, double[] alpha, int i) {
		double[] g = new double[4];
		double x1 = x - alpha[i];
		double x2 = x - alpha[i - 1];
		double wi_1 = x1 / (alpha[i + 1] - alpha[i]);
		double wi_2 = x1 / (alpha[i + 2] - alpha[i]);
		double wi1_2 = x2 / (alpha[i + 1] - alpha[i - 1]);
		double wi_3 = x1 / (alpha[i + 3] - alpha[i]);
		double wi1_3 = x2 / (alpha[i + 2] - alpha[i - 1]);
		double wi2_3 = (x - alpha[i - 2]) / (alpha[i + 1] - alpha[i - 2]);
		double w21 = (1 - wi1_2) * (1 - wi_1);
		double w22 = wi1_2 * (1 - wi_1) + (1 - wi_2) * wi_1;
		double w23 = wi_2 * wi_1;
		g[0] = (1 - wi2_3) * w21;
		g[1] = wi2_3 * w21 + (1 - wi1_3) * w22;
		g[2] = wi1_3 * w22 + (1 - wi_3) * w23;
		g[3] = wi_3 * w23;
		return g;
	}

	public static void hmb(double[] apX, double[] apY, double[] apPower,
			short[] apChannel, double apElevation, double[] gridX,
			double[] gridY, double[][] B, boolean useA, double mapWidthMetric,
			double mapHeightMetric, double imageWidth, double imageHeight,
			short mapColors[][], short mapChannels[][],
			boolean measureIntegral, double smallSegmentDrr,
			double mediumSegmentDrr, double dle) {
		hmb(apX, apY, apPower, apChannel, apElevation, gridX, gridY, B, useA,
				mapWidthMetric, mapHeightMetric, imageWidth, imageHeight,
				mapColors, mapChannels, measureIntegral, smallSegmentDrr,
				mediumSegmentDrr, dle, null);
	}

	public static void hmb(double[] apX, double[] apY, double[] apPower,
			short[] apChannel, double apElevation, double[] gridX,
			double[] gridY, double[][] B, boolean useA, double mapWidthMetric,
			double mapHeightMetric, double imageWidth, double imageHeight,
			short mapColors[][], short mapChannels[][],
			boolean measureIntegral, double smallSegmentDrr,
			double mediumSegmentDrr, double dle, WallLoss[] walls) {
		double C = useA ? 20 * Math.log10(5500) - 28
				: 20 * Math.log10(2400) - 28;
		double ten = 10 / Math.log(10);

		double imageToMetric = Math.min(mapWidthMetric / imageWidth,
				mapHeightMetric / imageHeight);
		SegmentContext context = new SegmentContext();
		context.verbose = false;
		context.A = null;
		context.B = B;
		context.gridX = gridX;
		context.gridY = gridY;
		context.segmentCount = 0;
		context.smallSegmentCount = 0;
		context.mediumSegmentCount = 0;
		context.verifyFit = false;
		context.measureIntegral = measureIntegral;
		if (smallSegmentDrr > 0) {
			context.smallSegmentDrr = smallSegmentDrr;
		}
		if (mediumSegmentDrr > 0) {
			context.mediumSegmentDrr = mediumSegmentDrr;
		}

		int xIndex = 3; // 3 extra 0 elements
		double gridRight = gridX[xIndex + 1];
		for (double x = 0; x < imageWidth; x += 1) {
			context.rx = (x + 0.5) * imageToMetric;
			while (context.rx >= gridRight) {
				gridRight = gridX[++xIndex + 1];
			}
			int yIndex = 3; // 3 extra 0 elements
			double gridBottom = gridY[yIndex + 1];
			for (double y = 0; y < imageHeight; y += 1) {
				context.ry = (y + 0.5) * imageToMetric;
				while (context.ry >= gridBottom) {
					gridBottom = gridY[++yIndex + 1];
				}
				double maxRssi = -100000;
				short channel = -1;
				for (short i = 0; i < apX.length; i++) {
					if (apPower[i] > 0 && apChannel[i] > 0) {
						context.xIndex = xIndex;
						context.yIndex = yIndex;
						context.tx = apX[i];
						context.ty = apY[i];
						context.th = apElevation;
						segments(context, gridX, gridY);
						double d = Math.sqrt(Math.pow(context.rx - context.tx,
								2)
								+ Math.pow(context.ry - context.ty, 2)
								+ Math.pow(context.th, 2));
						double dl = 0;
						if (d > 1 && dle > 0) {
							dl = 10 * dle * Math.log10(d);
						}
						double wallLoss = 0;
						if (walls != null) {
							wallLoss = getWallLoss(walls, context, false);
						}
						double rssi = apPower[i] - wallLoss
								- (C + ten * context.loss + dl);
						if (rssi > maxRssi) {
							maxRssi = rssi;
							// channel = apChannel[i];
							channel = i;
						}
					}
				}
				short rssiColor = -1;
				if (maxRssi != -100000) {
					if (maxRssi > -35) {
						maxRssi = -35;
					}
					rssiColor = (short) (-35 - maxRssi);
				}
				mapColors[(int) x][(int) y] = rssiColor;
				mapChannels[(int) x][(int) y] = channel;
			}
		}
		String fmt = "%% B is %dx%d and %d segments, %d small, %d medium, %d remaining.";
		log.info_ln(String.format(fmt, B.length, B[0].length,
				context.segmentCount, context.smallSegmentCount,
				context.mediumSegmentCount, context.segmentCount
						- context.smallSegmentCount
						- context.mediumSegmentCount));
		if (context.verifyFit) {
			logt.info("hmb", "hmb: Max rcond of small segments: "
					+ context.maxRcond + ", dgesv failures: "
					+ context.dgesvFailures);
		} else if (context.measureIntegral) {
			fmt = "hmb: max error per segment: %.4f%%, small : %.2f%%, medium : %.2f%%.";
			logt.info("hmb", String.format(fmt, context.maxSegmentError * 100,
					context.maxSmallSegmentError * 100,
					context.maxMediumSegmentError * 100));
		}
	}

	public static void hmbr(double[] apX, double[] apY, double[] apPower,
			short[] apChannel, double apElevation, double[] gridX,
			double[] gridY, double[][] B, boolean useA, double mapWidthMetric,
			double mapHeightMetric, double imageWidth, double imageHeight,
			short mapColors[][], short mapChannels[][], double mapRssi[][],
			boolean measureIntegral, double smallSegmentDrr,
			double mediumSegmentDrr, double dle, WallLoss[] walls, short from,
			short until) {
		double C = useA ? 20 * Math.log10(5500) - 28
				: 20 * Math.log10(2400) - 28;
		C = 20 * Math.log10(getFrequency((short) 1)) - 28;
		double ten = 10 / Math.log(10);

		double imageToMetric = Math.min(mapWidthMetric / imageWidth,
				mapHeightMetric / imageHeight);
		SegmentContext context = new SegmentContext();
		context.verbose = false;
		context.A = null;
		context.B = B;
		context.gridX = gridX;
		context.gridY = gridY;
		context.segmentCount = 0;
		context.smallSegmentCount = 0;
		context.mediumSegmentCount = 0;
		context.verifyFit = false;
		context.measureIntegral = measureIntegral;
		if (smallSegmentDrr > 0) {
			context.smallSegmentDrr = smallSegmentDrr;
		}
		if (mediumSegmentDrr > 0) {
			context.mediumSegmentDrr = mediumSegmentDrr;
		}

		int xIndex = 3; // 3 extra 0 elements
		double gridRight = gridX[xIndex + 1];
		for (double x = 0; x < imageWidth; x += 1) {
			context.rx = (x + 0.5) * imageToMetric;
			while (context.rx >= gridRight) {
				gridRight = gridX[++xIndex + 1];
			}
			int yIndex = 3; // 3 extra 0 elements
			double gridBottom = gridY[yIndex + 1];
			for (double y = 0; y < imageHeight; y += 1) {
				context.ry = (y + 0.5) * imageToMetric;
				while (context.ry >= gridBottom) {
					gridBottom = gridY[++yIndex + 1];
				}
				double maxRssi = -100000;
				short channel = -1;
				for (short i = from; i <= until; i++) {
					if (apPower[i] > 0 && apChannel[i] > 0) {
						context.xIndex = xIndex;
						context.yIndex = yIndex;
						context.tx = apX[i];
						context.ty = apY[i];
						context.th = apElevation;
						segments(context, gridX, gridY);
						double d = Math.sqrt(Math.pow(context.rx - context.tx,
								2)
								+ Math.pow(context.ry - context.ty, 2)
								+ Math.pow(context.th, 2));
						double dl = 0;
						if (d > 1 && dle > 0) {
							dl = 10 * dle * Math.log10(d);
						}
						double wallLoss = 0;
						if (walls != null) {
							wallLoss = getWallLoss(walls, context, false);
						}
						double rssi = apPower[i] - wallLoss
								- (C + ten * context.loss + dl);
						if (rssi > maxRssi) {
							maxRssi = rssi;
							// channel = apChannel[i];
							channel = i;
						}
					}
				}
				short rssiColor = -1;
				if (maxRssi != -100000) {
					if (maxRssi > mapRssi[(int) x][(int) y]) {
						mapRssi[(int) x][(int) y] = maxRssi;
						if (maxRssi > -35) {
							maxRssi = -35;
						}
						rssiColor = (short) (-35 - maxRssi);
						mapColors[(int) x][(int) y] = rssiColor;
						mapChannels[(int) x][(int) y] = channel;
					}
				}
			}
		}
		String fmt = "%% B is %dx%d and %d segments, %d small, %d medium, %d remaining.";
		log.info_ln(String.format(fmt, B.length, B[0].length,
				context.segmentCount, context.smallSegmentCount,
				context.mediumSegmentCount, context.segmentCount
						- context.smallSegmentCount
						- context.mediumSegmentCount));
		if (context.verifyFit) {
			logt.info("hmb", "hmb: Max rcond of small segments: "
					+ context.maxRcond + ", dgesv failures: "
					+ context.dgesvFailures);
		} else if (context.measureIntegral) {
			fmt = "hmb: max error per segment: %.4f%%, small : %.2f%%, medium : %.2f%%.";
			logt.info("hmb", String.format(fmt, context.maxSegmentError * 100,
					context.maxSmallSegmentError * 100,
					context.maxMediumSegmentError * 100));
		}
	}

	public static double[] lv(double[] apX, double[] apY, double xm, double ym,
			double apElevation, double[] gridX, double[] gridY, double[][] B,
			Graphics2D g2, double metricToImage) {
		SegmentContext context = new SegmentContext();
		context.verbose = false;
		context.B = B;
		context.gridX = gridX;
		context.gridY = gridY;
		context.tx = xm;
		context.ty = ym;
		context.th = apElevation;
		if (g2 != null) {
			g2.setColor(Color.black);
			Point2D be = new Point2D.Double(context.tx, context.ty);
			fillOval(g2, be, metricToImage);
		}
		double[] lv = new double[apX.length];
		for (int i = 0; i < apX.length; i++) {
			context.rx = apX[i];
			context.ry = apY[i];
			int xIndex = findGridIndex(context.rx, gridX);
			int yIndex = findGridIndex(context.ry, gridY);
			context.xIndex = xIndex;
			context.yIndex = yIndex;
			segments(context, gridX, gridY);
			lv[i] = 10 * context.loss / Math.log(10);
		}
		return lv;
	}

	public static double[][][] hmv(double[] apX, double[] apY,
			double apElevation, double[] gridX, double[] gridY, double[][] B,
			int gridXcount, double gridXsize, int gridYcount, double gridYsize,
			boolean calibrateHeatmap, double lsle, double dle, Graphics2D g2,
			double metricToImage) {
		return hmv(apX, apY, apElevation, gridX, gridY, B, gridXcount,
				gridXsize, gridYcount, gridYsize, calibrateHeatmap, lsle, dle,
				g2, metricToImage, null);
	}

	public static double[][][] hmv(double[] apX, double[] apY,
			double apElevation, double[] gridX, double[] gridY, double[][] B,
			int gridXcount, double gridXsize, int gridYcount, double gridYsize,
			boolean calibrateHeatmap, double lsle, double dle, Graphics2D g2,
			double metricToImage, WallLoss[] walls) {
		SegmentContext context = new SegmentContext();
		context.verbose = false;
		context.B = B;
		context.gridX = gridX;
		context.gridY = gridY;
		double[][][] hmv = new double[gridXcount + 1][gridYcount + 1][apX.length];
		double xm = -gridXsize / 2;
		for (int xi = 0; xi <= gridXcount; xi++) {
			if (xi == 0) {
				context.tx = 0;
			} else if (xi == gridXcount) {
				context.tx = xm - gridXsize / 2;
			} else {
				context.tx = xm;
			}
			double ym = -gridYsize / 2;
			for (int yi = 0; yi <= gridYcount; yi++) {
				if (yi == 0) {
					context.ty = 0;
				} else if (yi == gridYcount) {
					context.ty = ym - gridYsize / 2;
				} else {
					context.ty = ym;
				}
				context.th = apElevation;
				if (g2 != null) {
					g2.setColor(Color.black);
					Point2D be = new Point2D.Double(context.tx, context.ty);
					fillOval(g2, be, metricToImage);
				}
				for (int i = 0; i < apX.length; i++) {
					context.rx = apX[i];
					context.ry = apY[i];
					double d = Math.sqrt(Math.pow(context.rx - context.tx, 2)
							+ Math.pow(context.ry - context.ty, 2)
							+ Math.pow(context.th, 2));
					double wallLoss = getWallLoss(walls, context, false);
					if (calibrateHeatmap) {
						int xIndex = findGridIndex(context.rx, gridX);
						int yIndex = findGridIndex(context.ry, gridY);
						context.xIndex = xIndex;
						context.yIndex = yIndex;
						segments(context, gridX, gridY);
						hmv[xi][yi][i] = wallLoss + 10 * context.loss
								/ Math.log(10);
						if (d > 1 && dle > 0) {
							hmv[xi][yi][i] += 10 * dle * Math.log10(d);
						}
					} else {
						if (d > 1) {
							hmv[xi][yi][i] = wallLoss + 10 * lsle
									* Math.log10(d);
						} else {
							hmv[xi][yi][i] = wallLoss;
						}
					}
					// log.debug("hmv", "position: (" + xm + ", " + ym +
					// "), loss: " + hmv[xi][yi][i]);
				}
				ym += gridYsize;
			}
			xm += gridXsize;
		}
		return hmv;
	}

	public static double[] hmm(double[][][] hmv, double gridXsize,
			double gridYsize, ClientDetected[] clients, double min_t,
			double max_t, Graphics2D g2, double metricToImage, boolean verbose) {
		double mt = 0, mfx = Double.MAX_VALUE;
		int mi = 0, mj = 0;
		for (int i = 0; i < hmv.length; i++) {
			for (int j = 0; j < hmv[i].length; j++) {
				double[] v = hmv[i][j];
				double abx = 0;
				double abxe = 0;
				for (ClientDetected client : clients) {
					double d = client.getRssi() + client.getC()
							+ v[client.apIndex];
					abx += d * d;
					abxe += d;
				}
				double t = abxe / clients.length;
				double fx = abx - abxe * t;

				if (verbose) {
					double xm = gridXsize * (i - 0.5);
					double ym = gridYsize * (j - 0.5);
					log.info("fx[" + xm + "," + ym + "] = " + fx + ", t = " + t);
				}
				if (fx < mfx && t >= min_t && t < max_t) {
					mfx = fx;
					mt = t;
					mi = i;
					mj = j;
				}
			}
		}
		double xm = gridXsize * (mi - 0.5);
		double ym = gridYsize * (mj - 0.5);
		if (g2 != null) {
			g2.setColor(Color.cyan);
			Point2D be = new Point2D.Double(xm, ym);
			fillOval(g2, be, metricToImage);
		}
		return new double[] { xm, ym, mt };
	}

	public static double[] hmm(double[][][] hmv, double gridXsize,
			double gridYsize, ClientDetected[] clients, double erp,
			Graphics2D g2, double metricToImage, boolean verbose) {
		double mabx = Double.MAX_VALUE;
		int mi = 0, mj = 0;
		for (int i = 0; i < hmv.length; i++) {
			for (int j = 0; j < hmv[i].length; j++) {
				double[] v = hmv[i][j];
				double abx = 0;
				for (ClientDetected client : clients) {
					double d = client.getRssi() + client.getC()
							+ v[client.apIndex] - erp;
					abx += d * d;
				}
				if (verbose) {
					double xm = gridXsize * (i - 0.5);
					double ym = gridYsize * (j - 0.5);
					log.info_ln("abx[" + xm + "," + ym + "] = " + abx);
				}
				if (abx < mabx) {
					mabx = abx;
					mi = i;
					mj = j;
				}
			}
		}
		double xm = gridXsize * (mi - 0.5);
		double ym = gridYsize * (mj - 0.5);
		if (g2 != null) {
			g2.setColor(Color.cyan);
			Point2D be = new Point2D.Double(xm, ym);
			fillOval(g2, be, metricToImage);
		}
		return new double[] { xm, ym, erp };
	}

	public static double[] hmm_a(double[][][] hmv, double[][] hma,
			double gridXsize, double gridYsize, ClientDetected[] clients,
			double min_t, double max_t, Graphics2D g2, double metricToImage,
			double area, boolean verbose) {
		double mt = 0, mfx = Double.MAX_VALUE, max_fx = -Double.MAX_VALUE;
		int mi = 0, mj = 0;
		for (int i = 0; i < hmv.length; i++) {
			for (int j = 0; j < hmv[i].length; j++) {
				double[] v = hmv[i][j];
				double abx = 0;
				double abxe = 0;
				for (ClientDetected client : clients) {
					double d = client.getRssi() + client.getC()
							+ v[client.apIndex];
					abx += d * d;
					abxe += d;
				}
				double t = abxe / clients.length;
				double fx = abx - abxe * t;

				if (verbose) {
					double xm = gridXsize * (i - 0.5);
					double ym = gridYsize * (j - 0.5);
					log.info("fx[" + xm + "," + ym + "] = " + fx + ", t = " + t);
				}
				if (t >= min_t && t < max_t) {
					if (fx < mfx) {
						mfx = fx;
						mt = t;
						mi = i;
						mj = j;
					}
					if (fx > max_fx) {
						max_fx = fx;
					}
				}
			}
		}
		double xm = gridXsize * (mi - 0.5);
		double ym = gridYsize * (mj - 0.5);
		if (g2 != null) {
			g2.setColor(Color.cyan);
			Point2D be = new Point2D.Double(xm, ym);
			fillOval(g2, be, metricToImage);
		}
		if (hma != null) {
			mfx = Math.sqrt(mfx);
			max_fx = Math.sqrt(max_fx);
			log.info_ln("% min_fx: " + mfx + ", max_fx: " + max_fx);
			for (int i = 0; i < hmv.length; i++) {
				for (int j = 0; j < hmv[i].length; j++) {
					double[] v = hmv[i][j];
					double abx = 0;
					double abxe = 0;
					for (ClientDetected client : clients) {
						double d = client.getRssi() + client.getC()
								+ v[client.apIndex];
						abx += d * d;
						abxe += d;
					}
					double t = abxe / clients.length;
					double fx = Math.sqrt(abx - abxe * t);

					if (t >= min_t && t < max_t) {
						if (fx - mfx < area * (max_fx - mfx)) {
							hma[i][j] = t;
						}
					}
				}
			}
		}
		return new double[] { xm, ym, mt };
	}

	public static double[] hmm_b(double[][][] hmv, double[][] hma,
			double gridXsize, double gridYsize, ClientDetected[] clients,
			double erp, Graphics2D g2, double metricToImage, double area,
			boolean verbose) {
		double mabx = Double.MAX_VALUE, max_abx = -Double.MAX_VALUE;
		int mi = 0, mj = 0;
		for (int i = 0; i < hmv.length; i++) {
			for (int j = 0; j < hmv[i].length; j++) {
				double[] v = hmv[i][j];
				double abx = 0;
				for (ClientDetected client : clients) {
					double d = client.getRssi() + client.getC()
							+ v[client.apIndex] - erp;
					abx += d * d;
				}
				if (verbose) {
					double xm = gridXsize * (i - 0.5);
					double ym = gridYsize * (j - 0.5);
					log.info_ln("abx[" + xm + "," + ym + "] = " + abx);
				}
				if (abx < mabx) {
					mabx = abx;
					mi = i;
					mj = j;
				}
				if (abx > max_abx) {
					max_abx = abx;
				}
			}
		}
		double xm = gridXsize * (mi - 0.5);
		double ym = gridYsize * (mj - 0.5);
		if (g2 != null) {
			g2.setColor(Color.cyan);
			Point2D be = new Point2D.Double(xm, ym);
			fillOval(g2, be, metricToImage);
		}
		if (hma != null) {
			mabx = Math.sqrt(mabx);
			max_abx = Math.sqrt(max_abx);
			log.info_ln("% min_abx: " + mabx + ", max_abx: " + max_abx);
			for (int i = 0; i < hmv.length; i++) {
				for (int j = 0; j < hmv[i].length; j++) {
					double[] v = hmv[i][j];
					double abx = 0;
					for (ClientDetected client : clients) {
						double d = client.getRssi() + client.getC()
								+ v[client.apIndex] - erp;
						abx += d * d;
					}
					abx = Math.sqrt(abx);
					if (abx - mabx < area * (max_abx - mabx)) {
						hma[i][j] = abx;
					}
					if (abx < mabx) {
						mabx = abx;
						mi = i;
						mj = j;
					}
					if (abx > max_abx) {
						max_abx = abx;
					}
				}
			}
		}
		return new double[] { xm, ym, erp };
	}

	public static void ler(double[] gridX, double[] gridY, double[][] B,
			double mapWidthMetric, double mapHeightMetric, double imageWidth,
			double imageHeight) {
		double imageToMetric = Math.min(mapWidthMetric / imageWidth,
				mapHeightMetric / imageHeight);
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		int diffs = 0;
		double precision = 1e-14;
		SegmentContext context = new SegmentContext();
		context.B = B;
		context.gridX = gridX;
		context.gridY = gridY;
		context.xIndex = 3; // 3 extra 0 elements
		double gridRight = gridX[context.xIndex + 1];
		for (double x = 0; x < imageWidth; x += 1) {
			double rx = (x + 0.5) * imageToMetric;
			while (rx >= gridRight) {
				gridRight = gridX[++context.xIndex + 1];
			}
			context.yIndex = 3; // 3 extra 0 elements
			double gridBottom = gridY[context.yIndex + 1];
			for (double y = 0; y < imageHeight; y += 1) {
				double ry = (y + 0.5) * imageToMetric;
				while (ry >= gridBottom) {
					gridBottom = gridY[++context.yIndex + 1];
				}
				// double le = le(context, rx, ry);
				double le = le_b(context, rx, ry);
				// if (Math.abs(le - le_b) > precision) {
				// diffs++;
				// }
				if (le < min) {
					min = le;
				}
				if (le > max) {
					max = le;
				}
			}
		}
		log.info_ln(String.format("%% loss range: (%.4f, %.4f)", min, max));
	}
}

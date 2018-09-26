import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static String varNumbs = null;
	public static String datapoints = null;
	public static String threshold = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		varNumbs = request.getParameter("variables");
		datapoints = request.getParameter("datapoints");
		threshold = request.getParameter("threshold");
		Part filePart = request.getPart("file");
		InputStream fileContent = filePart.getInputStream();

		Scanner scanner = new Scanner(fileContent);
		Scanner s = scanner.useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		s.close();
		scanner.close();

		String[] array = result.split("\r\n");

		// Input Validation
		if (varNumbs == null || varNumbs.equals("") || Integer.parseInt(varNumbs) <= 0) {
			request.setAttribute("message", "Invalid input - Number of variables need to be >0");
			getServletContext().getRequestDispatcher("/responserror.jsp").forward(request, response);
			return;
		}
		if (datapoints == null || datapoints.equals("") || Integer.parseInt(datapoints) <= 0) {
			request.setAttribute("message", "Invalid input - Number of datapoints need to be >0");
			getServletContext().getRequestDispatcher("/responserror.jsp").forward(request, response);
			return;
		}
		if (threshold == null || threshold.equals("") || Double.parseDouble(threshold) < 0) {
			request.setAttribute("message", "Invalid input - Missing or negative threshold value");
			getServletContext().getRequestDispatcher("/responserror.jsp").forward(request, response);
			return;
		}
		if (result == null || result.equals("")) {
			request.setAttribute("message", "Invalid input - File does not contain any value");
			getServletContext().getRequestDispatcher("/responserror.jsp").forward(request, response);
			return;
		}
		if (array.length != Integer.parseInt(datapoints)) {
			request.setAttribute("message", "Invalid input - File does not match number of datapoints");
			getServletContext().getRequestDispatcher("/responserror.jsp").forward(request, response);
			return;
		}

		// Filling the 2D array with input values
		double[][] array2d = new double[Integer.parseInt(varNumbs)][Integer.parseInt(datapoints)];

		for (int i = 0; i < array.length; i++) {
			String[] tempArr = array[i].split(",");
			if (tempArr.length != Integer.parseInt(varNumbs)) {
				request.setAttribute("message", "Invalid input - File content does not match number of variables");
				getServletContext().getRequestDispatcher("/responserror.jsp").forward(request, response);
				return;
			} else {
				for (int j = 0; j < tempArr.length; j++) {
					array2d[j][i] = Double.parseDouble(tempArr[j]);
				}
			}
		}

		// Calculate assoPattern a group of 2 columns of the 2D array
		StringBuilder strout = new StringBuilder("<h4>Accepted:</h4>");
		StringBuilder stroutrjct = new StringBuilder("<h4>Rejected:</h4>");
		ArrayList<ArrayList<Integer>> combine = combine(Integer.parseInt(varNumbs), 2);
		for (ArrayList<Integer> sub : combine) {
			double[][] temp = new double[2][array2d[0].length];
			temp[0] = Arrays.copyOfRange(array2d[sub.get(0).intValue()], 0, array2d[sub.get(0).intValue()].length);
			temp[1] = Arrays.copyOfRange(array2d[sub.get(1).intValue()], 0, array2d[sub.get(1).intValue()].length);
			strout.append("<h5>Test for Vars: " + (sub.get(0) + 1) + ", " + (sub.get(1) + 1) + "</h5>");
			assoPattern2(temp, Double.parseDouble(threshold), strout, stroutrjct);
		}

		request.setAttribute("message", strout.toString() + stroutrjct.toString());
		request.setAttribute("threshold", threshold);
		getServletContext().getRequestDispatcher("/response.jsp").forward(request, response);
	}

	// Association pattern discovery for order 2
	public void assoPattern2(double[][] array, double threshold, StringBuilder stro, StringBuilder strorjc) {

		for (int i = 0; i < array[0].length; i++) {
			double xi = array[0][i];
			double yi = array[1][i];

			// calculate joint probability P(x=xi,y=yi)
			int count = 0;
			int countxi = 0;
			int countyi = 0;
			String ret = "";
			for (int a = 0; a < array[0].length; a++) {
				if (array[0][a] == xi && array[1][a] == yi)
					++count;
				if (array[0][a] == xi)
					++countxi;
				if (array[1][a] == yi)
					++countyi;
			}
			double jointProb = (double) count / array[0].length;
			// calculate probability P(x=xi)
			double probxi = (double) countxi / array[0].length;
			// calculate probability P(y=yi)
			double probyi = (double) countyi / array[0].length;
			ret += (int) xi + "," + (int) yi + " - " + "jointProbability: " + jointProb + " probXi: " + probxi
					+ " probYi: " + probyi;

			// calculate Association(xi,yi)
			double asso = Math.log(jointProb / (probxi * probyi)) / Math.log(2);
			ret += " Asso: " + asso;

			// calculate Chi^2 for xi,yi
			double chi2 = ((Math.pow((jointProb - (probxi * probyi)), 2)) / (probxi * probyi)) * array[0].length;
			ret += " ChiSquare: " + chi2;

			// Test1 - Asso - (chi^2/2n) > 0
			double test1 = asso - (chi2 / (2 * (array[0].length)));
			ret += " Test: " + test1;

			// Test2 - JointProbability > Threshold
			if (test1 > 0 && jointProb > threshold) {
				ret += " - Accepted <br /><br />";
				stro.append(ret);
			} else {
				ret += " - Rejected <br /><br />";
				strorjc.append(ret);
			}

		}
	}

	// combination n choose k
	public static ArrayList<ArrayList<Integer>> combine(int n, int k) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

		if (n <= 0 || n < k)
			return result;

		ArrayList<Integer> item = new ArrayList<Integer>();
		dfs(n, k, 1, item, result); // because it need to begin from 1

		for (ArrayList<Integer> arr : result) {
			int index = 0;
			for (Integer inter : arr) {
				arr.set(index, inter - 1);
				++index;
			}
		}

		return result;
	}

	private static void dfs(int n, int k, int start, ArrayList<Integer> item, ArrayList<ArrayList<Integer>> res) {
		if (item.size() == k) {
			res.add(new ArrayList<Integer>(item));
			return;
		}

		for (int i = start; i <= n; i++) {
			item.add(i);
			dfs(n, k, i + 1, item, res);
			item.remove(item.size() - 1);
		}
	}
}
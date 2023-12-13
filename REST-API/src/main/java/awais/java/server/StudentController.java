// Updated StudentController
package awais.java.server;  // Change the package name accordingly

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/students/*")
public class StudentController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	private StudentDAO studentDAO;
	
	private Gson gson;
	
	public void init() {
		studentDAO = new StudentDAO();
		gson = new Gson();
	}
	
	private void sendAsJSON(HttpServletResponse response, Object obj) throws ServletException, IOException {
		response.setContentType("application/json");
		String result = gson.toJson(obj);
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
	}
	
	// Get students
	// GET/RestAPI/students/
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String pathInfo = request.getPathInfo();
			
	response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
	response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");
			
	// Return all users
	// GET/RestAPI/students/
	if(pathInfo == null || pathInfo.equals("/")) {
			List<students> students = studentDAO.selectAllStudents();
			sendAsJSON(response, students);
			return;
	}
	
	// Return student by id
	// GET/RestAPI/students/id 
	String splits[] = pathInfo.split("/");
	if(splits.length != 2) {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		return;
	}
			
	int id = Integer.parseInt(splits[1]);
	students student = studentDAO.selectStudentByID(id);
			if(student == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			} else {
				sendAsJSON(response, student);
				return;
			}
		}
		
	// Post new student
	// POST/RestAPI/students/
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//		res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//		res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");
//			
		String pathInfo = request.getPathInfo();
//		System.out.println(pathInfo);
		if(pathInfo == null | pathInfo == "/") {
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
//				
			String line;
			while((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			String payload = buffer.toString();
			students student = gson.fromJson(payload, students.class);
			studentDAO.insertStudent(student);
			sendAsJSON(response, "Success");
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}
	// Delete student
	// DELETE/RestAPI/students/
	 @Override
	    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        String pathInfo = request.getPathInfo();
	        //System.out.println(pathInfo);

	        //response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
	        //response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");

	        // Check if pathInfo is in the correct format (e.g., /id)
	        if (pathInfo != null && pathInfo.matches("/\\d+")) {
	            // Extract student ID from the path
	            int id = Integer.parseInt(pathInfo.substring(1));

	            // Check if the student exists
	            students existingStudent = studentDAO.selectStudentByID(id);
	            if (existingStudent == null) {
	                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
	                return;
	            }

	            // Delete the student
	            studentDAO.deleteStudent(id);
	            sendAsJSON(response, "Student deleted successfully");
	        } else {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
	        }
	    }
	
	
}
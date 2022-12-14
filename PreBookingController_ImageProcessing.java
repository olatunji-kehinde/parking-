package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import vo.AttachmentVO;
import vo.ParkingVO;
import vo.PersonVO;
import vo.PreBookingVO;
import vo.SlotsVO;
import vo.TransactionVO;
import vo.UserRole;
import vo.UserVO;
import vo.VehicleVO;
import dao.AttachmentDAO;
import dao.BookingDAO;
import dao.PersonDAO;
import dao.PreBookingDAO;
import dao.SlotsDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import dao.VehicleDAO;



/**
 * Servlet implementation class Circular
 */
@WebServlet("/PreBookingController_ImageProcessing")
public class PreBookingController_ImageProcessing extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PreBookingController_ImageProcessing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// mode=0 Add New
		//mode=1 Insert
		//mode=2 Edit
		//mode=3 update
		//mode=4 Delete
		//mode=5 Show 		
		
		//Mode Type defined to String to control over illegal parameter passing through URL
		String mode="0";
		 
		//Getting the mode variable, if not than default to 0(zero)  
		if(request.getParameter("mode")!=null)
		   mode = request.getParameter("mode");
		else
			mode="0";
		
		
		if(mode.equals("2")){
			edit(request, response);
		}
		else if(mode.equals("4")) {
			delete(request, response);
			
		}else if(mode.equals("5")){
			show(request, response);
		}else if(mode.equals("6")){
			preBook(request, response);
		}else if(mode.equals("7")){
			getVehicle(request, response);
		}else{
			//default Add
			
		
			response.sendRedirect(request.getContextPath()+"/views/employee/PreBooking_ImageProcessing/takePicture.jsp");
			
		}
		
		
		
		
		
	}
	
	
protected void registerUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		/*This code fetches the user Id of logged in User 
		Begin*/ 
		HttpSession session = request.getSession();
		long userID;
		if (session.getAttribute("userID") != null) {

			userID = (long) session.getAttribute("userID");

		} else {
			userID=1;
		}
		/*ENDS fetching user id*/
		
		String email = request.getParameter("email");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String mobileNumber = request.getParameter("mobileNumber");
		String password = request.getParameter("password");
		String userName = request.getParameter("userName");
		
		PersonVO personVO = new PersonVO();
		personVO.setEmail(email);
		personVO.setFirstName(firstName);
		personVO.setLastName(lastName);
		personVO.setMobileNumber(mobileNumber);
		
		PersonDAO dao = new PersonDAO();
		dao.insert(personVO);
		
		UserVO VO_obj = new UserVO();
		VO_obj.setPassword(password);
		VO_obj.setPersonID(personVO);
		VO_obj.setUserName(userName);
		VO_obj.setUserType(UserRole.REGISTERED_USER);
		 AttachmentVO attachmentVO = new AttachmentVO();

			//getting file from Session
			
			
			
					List myList=(List)session.getAttribute("fileList");
					
					 Iterator itr =  myList.iterator();
			         
			         System.out.println("Reading for session");
			         int i =0;
			         while (itr.hasNext()) {
			        	 
			        	
			     		attachmentVO.setAttachmentType("Profile Picture");
			     		
			        	 //file name
			     		attachmentVO.setPath((String)itr.next());
			        	 
			        	
			        	 
			        	 AttachmentDAO attachmentDAO = new AttachmentDAO();
				     		attachmentDAO.insert(attachmentVO);
				     
			        	 
			        	 i++;
						}
					
			         VO_obj.setAttachmentID(attachmentVO);
					
					session.removeAttribute("fileList");
					
					VO_obj.setAttachmentID(attachmentVO);
		
		
		UserDAO DAO_obj=new UserDAO();
		DAO_obj.insert(VO_obj);
		

		long RG_userID = VO_obj.getUserID();
		String model = request.getParameter("model");
		String numberOfVehicle = (String)session.getAttribute("numberOfVehicle");
		
		UserVO userVO = new UserVO();
		userVO.setUserID(RG_userID);
		
		VehicleVO vehicleVO_obj = new VehicleVO();
		vehicleVO_obj.setModel(model);
		vehicleVO_obj.setNumberOfVehicle(numberOfVehicle);
		vehicleVO_obj.setUserID(userVO);
		
		
		
		
		
		
		VehicleDAO vehicleDAO_obj=new VehicleDAO();
		vehicleDAO_obj.insert(vehicleVO_obj);
		
		
		
		session.setAttribute("vehicleID", vehicleVO_obj.getVehicleID());
		session.setAttribute("selectedUserID", vehicleVO_obj.getUserID().getUserID());
		

		ParkingVO parkingVO = new ParkingVO();
		parkingVO.setParkingID((long)session.getAttribute("parkingID"));
		
		
		SlotsVO slotsVO = new SlotsVO();
		slotsVO.setParkingID(parkingVO);
		
		SlotsDAO slotDAO_obj=new SlotsDAO();
		List obj= slotDAO_obj.getAllSlotsByParkingID(slotsVO);
		
		
		BookingDAO bookingDAO = new BookingDAO();
		List bookedSlot = bookingDAO.showAllBookedSlots(parkingVO);
		

		PreBookingDAO preBookingDAO = new PreBookingDAO();
		List preBookedSlot = preBookingDAO.showAllByParkingVO(parkingVO);
		
		System.out.println(preBookedSlot);
		
		session.setAttribute("obj_preBooked", preBookedSlot);
		session.setAttribute("obj_Booked", bookedSlot);
		session.setAttribute("obj_all", obj);
		response.sendRedirect(request.getContextPath()+"/views/employee/PreBooking_ImageProcessing/registerPreBooking.jsp");
		
		
	}
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// mode=0 Add New
		//mode=1 Insert
		//mode=2 Edit
		//mode=3 update
		//mode=4 Delete
		//mode=5 Show 
		//Mode Type defined to String to control over illegal parameter passing through URL
		 String mode="0";
		
		//Getting the mode variable, if not than default to 0(zero) 
		if(request.getParameter("mode")!=null)
			   mode = request.getParameter("mode");
			else
				doGet(request, response);
			
			
			if(mode.equals("1")){
				       insert(request, response);
			}else if(mode.equals("3")){
						update(request, response);
			}else if(mode.equals("8")){
				registerUser(request, response);
			}else{
						//default Add
						response.sendRedirect(request.getContextPath()+"/views/admin/Category/addCategory.jsp");
						}//default
					
			
			
			
			
		
		
	}
	
protected void getVehicle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		/*This code fetches the user Id of logged in User 
		Begin*/ 
		HttpSession session = request.getSession();
		long userID;
		if (session.getAttribute("userID") != null) {

			userID = (long) session.getAttribute("userID");

		} else {
			userID=1;
		}
		/*ENDS fetching user id*/
		
		
		
		long usID = Long.valueOf(request.getParameter("userID"));
		session.setAttribute("selectedUserID", usID);
		
		UserVO user = new UserVO();
		user.setUserID(usID);
		
		VehicleDAO DAO_obj=new VehicleDAO();
		List obj= DAO_obj.getAllVehicleByUserID(user);
		
		session.setAttribute("obj_all", obj);
		response.sendRedirect(request.getContextPath()+"/views/employee/PreBooking/selectVehicle.jsp");
		
		
		
}
protected void preBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		/*This code fetches the user Id of logged in User 
		Begin*/ 
		HttpSession session = request.getSession();
		long userID;
		if (session.getAttribute("userID") != null) {

			userID = (long) session.getAttribute("userID");

		} else {
			userID=1;
		}
		/*ENDS fetching user id*/
		
		ImageToText toText = new ImageToText();
		
		List myList=(List)session.getAttribute("fileList");
		
		 Iterator itr =  myList.iterator();
		 String FileLocation = "NA";
		 
		 
        System.out.println("Reading for session");
        
        if(itr.hasNext()) {
       	 
       	
        	FileLocation = (String)itr.next();
       	 
    	session.removeAttribute("fileList");
        }
		
		
        FileLocation =  session.getAttribute("url")+FileLocation;
        
        FileLocation = FileLocation.replace('\\', '/');
		
		System.out.println(FileLocation);
		
		System.out.println("Number Plate " +toText.getNumberPlate(FileLocation));
		
		String temp = (((toText.getNumberPlate(FileLocation)).replaceAll(" ", "")).replace("?", "-")).trim();
		
		System.out.println("Cleaned "+temp);
		
		VehicleVO inputData = new VehicleVO();
		inputData.setNumberOfVehicle(temp);
		session.setAttribute("numberOfVehicle", temp);
		
		VehicleDAO dao = new VehicleDAO();
		List ls = dao.search(inputData);
		
		Iterator vitr = ls.iterator();
		
		if (vitr.hasNext()) {
			VehicleVO vehicleVO = (VehicleVO) vitr.next();
			System.out.println(vehicleVO);
			
			long vehicleID = vehicleVO.getVehicleID();
			session.setAttribute("vehicleID", vehicleID);
			session.setAttribute("selectedUserID", vehicleVO.getUserID().getUserID());
			
			
			ParkingVO parkingVO = new ParkingVO();
			parkingVO.setParkingID((long)session.getAttribute("parkingID"));
			
			
			SlotsVO slotsVO = new SlotsVO();
			slotsVO.setParkingID(parkingVO);
			
			SlotsDAO DAO_obj=new SlotsDAO();
			List obj= DAO_obj.getAllSlotsByParkingID(slotsVO);
			
			
			BookingDAO bookingDAO = new BookingDAO();
			List bookedSlot = bookingDAO.showAllBookedSlots(parkingVO);
			

			PreBookingDAO preBookingDAO = new PreBookingDAO();
			List preBookedSlot = preBookingDAO.showAllByParkingVO(parkingVO);
			
			System.out.println(preBookedSlot);
			
			session.setAttribute("obj_preBooked", preBookedSlot);
			session.setAttribute("obj_Booked", bookedSlot);
			session.setAttribute("obj_all", obj);
			response.sendRedirect(request.getContextPath()+"/views/employee/PreBooking_ImageProcessing/registerPreBooking.jsp");
			
			
		}else{
			
			response.sendRedirect(request.getContextPath()+"/views/employee/PreBooking_ImageProcessing/addUser.jsp");
			
		}
		
		
		
		
		
}
	
	protected void insert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		/*This code fetches the user Id of logged in User 
		Begin*/ 
		HttpSession session = request.getSession();
		long userID;
		if (session.getAttribute("userID") != null) {

			userID = (long) session.getAttribute("userID");

		} else {
			userID=1;
		}
		/*ENDS fetching user id*/
		
		long usID = (long)session.getAttribute("selectedUserID");
		long parking = (long)session.getAttribute("parkingID");
		long vehicleID =  (long)session.getAttribute("vehicleID");
		
		
		
		Calendar calendar = Calendar.getInstance();
       // System.out.println("Original = " + calendar.getTime());
 
        // Substract 2 hour from the current time
       // calendar.add(Calendar.HOUR, -2);
 
        // Add 30 minutes to the calendar time
       // calendar.add(Calendar.MINUTE, 30);
 
        // Add 300 seconds to the calendar time
       // calendar.add(Calendar.SECOND, 300);
        
        
        
		Date date = calendar.getTime();
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		
		String bookingTime = format.format(date);
		
		
		calendar.add(Calendar.MINUTE, 30);
		Date date2 = calendar.getTime();
		String parkingTime = format.format(date2);
		
		calendar.add(Calendar.MINUTE, 40);
		Date date3 = calendar.getTime();
		String extTime1 = format.format(date3);
		
		calendar.add(Calendar.MINUTE, 45);
		Date date4 = calendar.getTime();
		String extTime2 = format.format(date4);
		
		UserVO userVO = new UserVO();
		userVO.setUserID(usID);
		
		VehicleVO vehicleVO = new VehicleVO();
		vehicleVO.setVehicleID(vehicleID);
		
		ParkingVO parkingVO = new ParkingVO();
		parkingVO.setParkingID(parking);
		
		
		PreBookingVO VO_obj = new PreBookingVO();
		VO_obj.setUserID(userVO);
		VO_obj.setParkingID(parkingVO);
		VO_obj.setBookingTime(bookingTime);
		VO_obj.setParkingTime(parkingTime);
		VO_obj.setExtendedTime1(extTime1);
		VO_obj.setExtendedTime2(extTime2);
		VO_obj.setVehicleID(vehicleVO);
		VO_obj.setStatus(1);
		
		long slotID = Long.valueOf(request.getParameter("slotID"));
		
		SlotsVO slotsVO = new SlotsVO();
		slotsVO.setSlotID(slotID);
		VO_obj.setSlotID(slotsVO);
		
		
		
				
		PreBookingDAO DAO_obj=new PreBookingDAO();
		DAO_obj.insert(VO_obj);
		
		
		
		VehicleDAO vdao = new VehicleDAO();
		List vls = vdao.getElementByID(vehicleVO);
       
		
		Iterator itr = vls.iterator();
		VehicleVO vehicleVO2 =(VehicleVO) itr.next();
		String message = "Thanks for Pre Booking with our Parking System for your vehicle "+vehicleVO2.getModel()+" -> "+vehicleVO2.getNumberOfVehicle();
		
		
		
		
		UserDAO dao = new UserDAO();
		List ls = dao.getElementByID(userVO);
		
		 itr = ls.iterator();
		UserVO userVO2 =(UserVO) itr.next();
		
		
		
		
		ServletContext context=getServletContext();  
		
		if(context.getInitParameter("emailStatus").equalsIgnoreCase("on")){
		
		MailHandler.sendMail(userVO2.getPersonID().getEmail(), message,context.getInitParameter("masterEmail"),context.getInitParameter("masterPassword"));
	
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setSentTime(bookingTime);
		transactionVO.setTransactionType(1);
		transactionVO.setUserID(userVO2);
		transactionVO.setDescription(userVO2.getPersonID().getEmail()+" ---> "+message);
		
		TransactionDAO dao2 = new TransactionDAO();
		dao2.insert(transactionVO);
		
		}
		
		
		
		show(request, response);
		
	}
	
    protected void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
		
		/*This code fetches the user Id of logged in User 
		Begin*/ 
		HttpSession session = request.getSession();
		long userID;
		if (session.getAttribute("userID") != null) {

			userID = (long) session.getAttribute("userID");

		} else {
			userID=1;
		}
		/*ENDS fetching user id*/
    	
	
		long usID = Long.valueOf(request.getParameter("userID"));
		long preBookingID = Long.valueOf(request.getParameter("preBookingID"));
		long vehicleID = Long.valueOf(request.getParameter("vehicleID"));
		String bookingTime = request.getParameter("bookingTime");
		String parkingTime = request.getParameter("parkingTime");
		
		UserVO userVO = new UserVO();
		userVO.setUserID(usID);
		
		VehicleVO vehicleVO = new VehicleVO();
		vehicleVO.setVehicleID(vehicleID);
		
		PreBookingVO VO_obj = new PreBookingVO();
		VO_obj.setPreBookingID(preBookingID);
		VO_obj.setUserID(userVO);
		VO_obj.setBookingTime(bookingTime);
		VO_obj.setParkingTime(parkingTime);
		VO_obj.setVehicleID(vehicleVO);
		
		
		
		
		
		
		PreBookingDAO DAO_obj=new PreBookingDAO();
		DAO_obj.update(VO_obj);
		
		show(request, response);
	}
    
    protected void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	long preBookingID=Long.parseLong(request.getParameter("id"));
    	PreBookingVO VO_obj =new PreBookingVO();
		VO_obj.setPreBookingID(preBookingID);
		
		PreBookingDAO DAO_obj=new PreBookingDAO();
		List obj= DAO_obj.getElementByID(VO_obj);
		
		HttpSession session = request.getSession();
		session.setAttribute("obj", obj);
		response.sendRedirect(request.getContextPath()+"/views/admin/Category/editCategory.jsp");
	}
    
    protected void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		
		/*This code fetches the user Id of logged in User 
		Begin*/ 
		HttpSession session = request.getSession();
		long userID;
		if (session.getAttribute("userID") != null) {

			userID = (long) session.getAttribute("userID");

		} else {
			userID=1;
		}
		/*ENDS fetching user id*/
		
    	int preBookingID=Integer.parseInt(request.getParameter("id"));
    	PreBookingVO VO_obj =new PreBookingVO();
		VO_obj.setPreBookingID(preBookingID);
		
		PreBookingDAO DAO_obj=new PreBookingDAO();
		DAO_obj.delete(VO_obj);
		show(request, response);
		
		
	}
    protected void show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
		
    	
    	ParkingVO parkingVO = new ParkingVO();
		parkingVO.setParkingID((long)session.getAttribute("parkingID"));
		
		    	
		
    	PreBookingDAO DAO_obj=new PreBookingDAO();
    	List objprebooked = DAO_obj.showAllByParkingVO(parkingVO);
    	
    	BookingDAO bookingDAO = new BookingDAO();
		List objbooked = bookingDAO.showAll();
    	
    	List obj= DAO_obj.getPreBookingByParkingID(parkingVO);
		
    	
    	
		session.setAttribute("obj_all", obj);
		session.setAttribute("obj_booked", objbooked);
		session.setAttribute("obj_preBooked",objprebooked);
		
		response.sendRedirect(request.getContextPath()+"/views/employee/PreBooking/managePreBooking.jsp");
	
	}

}

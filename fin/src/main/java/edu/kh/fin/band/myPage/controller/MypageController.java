package edu.kh.fin.band.myPage.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import edu.kh.fin.band.board.model.vo.BoardDetail;
import edu.kh.fin.band.board.model.vo.Reply;
import edu.kh.fin.band.login.model.vo.User;
import edu.kh.fin.band.myPage.model.service.MyPageService;
import edu.kh.fin.band.myPage.model.vo.Ban;
import edu.kh.fin.band.myPage.model.vo.Band;
import edu.kh.fin.band.myPage.model.vo.Crite;
import edu.kh.fin.band.myPage.model.vo.Pagi;
import edu.kh.fin.band.searching.model.vo.Searching;

@Controller
@SessionAttributes({"loginUser"})
public class MypageController {
	
	
	@Autowired 
	private MyPageService service;
	
	private Logger logger = LoggerFactory.getLogger(MypageController.class);
	
	
	@GetMapping("/myPage")
	public String myPageController(@ModelAttribute("loginUser") User loginUser, Model model,Crite cri) {
			
		int total = service.getTotal();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("userNo", loginUser.getUserNo());
		map.put("cri", cri);			
		
		Pagi pageVO = new Pagi(cri, total);		
		
		List<Ban> banList = service.chBanList(loginUser.getUserNo());
		
		List<Band> bandMem = service.bandMem(loginUser.getUserNo());
		
		int bandNo = service.getbandNo(loginUser.getUserNo());
		
		List<Band> BandUserList = service.BandUserList(bandNo);
		
		List<BoardDetail> boardList = service.boardList(map);
		
		List<Reply> rList = service.ReplyList(loginUser.getUserNo());
					
		//model.addAttribute("bandMem2",bandMem2);
		model.addAttribute("bandMem",bandMem);
		model.addAttribute("banList",banList);
		model.addAttribute("boardList", boardList);
	    model.addAttribute("pageVO", pageVO);
	    model.addAttribute("rList", rList);
	    model.addAttribute("bandUserList", BandUserList);
			
		return "myPage/myPage";
	}
	
	
	@PostMapping("fin/updateInfo")
	public String updateInfo(@RequestParam Map<String, Object> paramMap
			, @ModelAttribute("loginUser") User loginUser
			,@RequestParam("region") String region,@RequestParam("gender") char gender,
			@RequestParam("inst") String inst,
			@RequestParam("genre") String genre, Model model,@RequestParam("uploadImage") MultipartFile uploadImage
			,HttpServletRequest req, /* 파일 저장 경로 탐색용 */
			RedirectAttributes ra, HttpSession session
			, HttpServletRequest re, @RequestParam("newNick") String newNick
			, @RequestParam("newPw") String newPw ,@RequestParam("newIntro") String Intro
			, SessionStatus status
			) throws IOException{
		
		logger.info("회원 정보 수정 수행");
		
		Searching updateInfo = new Searching();
		
		String webPath = "/resources/images/profileImage/";
		
		String folderPath = req.getSession().getServletContext().getRealPath(webPath);
		
		session = re.getSession();
		
		updateInfo.setGenre(genre);
		updateInfo.setInst(inst);
		updateInfo.setRegion(region);
		updateInfo.setGender(gender);
		
		paramMap.put("newIntro", Intro);
		paramMap.put("newPw", newPw);
		paramMap.put("userPw", loginUser.getUserPw());
		paramMap.put("userNo", loginUser.getUserNo());
		paramMap.put("gender", updateInfo.getGender());
		paramMap.put("genre", updateInfo.getGenre());
		paramMap.put("inst", updateInfo.getInst());
		paramMap.put("region", updateInfo.getRegion());
		paramMap.put("webPath", webPath);
		paramMap.put("folderPath", folderPath);
		paramMap.put("uploadImage", uploadImage);
		paramMap.put("newNick", newNick);
		
		int allResult = 0;
		
		int bandInfo = 0;
		
		int img = service.updateImg(paramMap, loginUser);
		
		int info = service.updateInfo(paramMap);	
		
		int checkBand = service.checkBand(paramMap);
				
		int position = service.updatePosition(paramMap);
		
		int changePw = service.changePw(paramMap);		
		
		int insertPosition = 0;
		
		if(position != 0) {
			
			insertPosition = 0;
		} else {
			
			insertPosition = service.insertPosition(paramMap);
		}
		
		if(img > 0) {
			loginUser.setProfileImg((String)paramMap.get("profileImg"));
		}
		
		
		
		allResult = img + info + position + changePw + insertPosition + bandInfo;
		
		String message = null;
		
		if(allResult > 3) {
			message = "회원 정보 변경 완료";
		} else {
			message = "회원 정보 변경 실패";
		}
		
				
		
		User NewloginUser = service.NewloginUser(paramMap);
		
		model.addAttribute("loginUser", NewloginUser);
		
		
		ra.addFlashAttribute("msg", message);
		return "redirect:/myPage";
	}
	
	
	//회원 탈퇴 서비스 호출
	@PostMapping("fin/secession")
	public String secession(@ModelAttribute("loginUser") User loginUser,
			SessionStatus status,
			HttpServletRequest req,
			HttpServletResponse resp,
			RedirectAttributes ra ) {
		
		logger.info("회원 탈퇴 수행");
		
		int result = service.secession(loginUser);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "탈퇴 되었습니다.";
			path = "/";
			
			// 세션 없애기
			status.setComplete();
						
			// 쿠키 없애기
			Cookie cookie = new Cookie("saveId", "");
			cookie.setMaxAge(0);
			cookie.setPath(req.getContextPath());
			resp.addCookie(cookie);
		} else {
			message = "탈퇴 실패";
			path = "/myPage";
		}
			ra.addFlashAttribute("msg", message);
		
			return "redirect:" + path;
		
	}
	

	@PostMapping("fin/ban")
	public String ban(@ModelAttribute("loginUser") User loginUser,
			@RequestParam("bannedUserNo") int bannedUserNo,
			SessionStatus status,
			HttpServletRequest req,
			HttpServletResponse resp,
			RedirectAttributes ra) {
		
		logger.info("회원 차단해체 수행");
			
		int result = service.updateBan(bannedUserNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			
			message = "차단 해제 완료";
			path = "/myPage";
			
		} else {
			
			message = "차단 해제 실패";
			path = "/myPage";
		}
		
		ra.addFlashAttribute("msg", message);
		
		return "redirect:" + path;
		
	}
	
	@GetMapping("fin/makeBand")
	public String makeBand(@RequestParam("bandName") String bandName,
			@RequestParam Map<String, Object> paramMap,
			@ModelAttribute("loginUser") User loginUser,
			@RequestParam("ment") String ment,
			SessionStatus status,
			HttpServletRequest req,
			HttpServletResponse resp,
			RedirectAttributes ra	
			) {
		
		logger.info("회원 밴드 생성 수행");
		
		paramMap.put("ment", ment);
		paramMap.put("userNo", loginUser.getUserNo());
		paramMap.put("userNick", loginUser.getUserNick());
		paramMap.put("bandName", bandName);
		
		
		int result = service.makeBand(paramMap);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			
			int bandNo = service.bandNo(loginUser.getUserNo());
		
			paramMap.put("bandNo", bandNo);
		
			int set = service.makeBandUser(paramMap);
			
			message = "생성 완료";
			path = "/myPage";
			
		} else {
			
			message = "생성 실패";
			path = "/myPage";
		}
		
		ra.addFlashAttribute("msg", message);
		
		return "redirect:" + path;
	
		
	}
	
	@PostMapping("fin/exile")
	public String exile(@ModelAttribute("loginUser") User loginUser,
			@RequestParam("exileNo") int exileNo, Model model,
			SessionStatus status,
			HttpServletRequest req,
			HttpServletResponse resp,
			RedirectAttributes ra) {
		
		logger.info("밴드멤버 추방 수행");
		
		int result = service.exile(exileNo);
		
		String message = null;
		String path = null;
			
			
		if(result > 0) {
			
			message = "추방 완료";
			path = "/myPage";
													
		} else {
			
			message = "추방 실패";
			path = "/myPage";
		}
		
		ra.addFlashAttribute("msg", message);
			
		return "redirect:" + path;
		
		
		
	}
	
	@GetMapping("fin/dismiss")
	public String dismiss(@RequestParam("bandNo") int bandNo,
			SessionStatus status,
			HttpServletRequest req,
			HttpServletResponse resp,
			RedirectAttributes ra
			) {
		
		logger.info("밴드 해체 수행");
		
		int result = service.dismiss(bandNo);
		
		int result2 = service.dissmiss2(bandNo);
		
		int total = 0;
		
		total = result + result2;
		
		String message = null;
		String path = null;
		
		if(total == 2) {
			
			message = "해체 완료";
			path = "/myPage";
			
		} else {
			
			message = "해체 성공";
			path = "/myPage";
		}
		
		ra.addFlashAttribute("msg", message);

		
		return "redirect:" + path;
		
	}
	
	
	
	
}

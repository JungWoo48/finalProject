package edu.kh.fin.band.searching.model.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.band.searching.model.vo.Searching;

@Repository
public class SearchingDAO {
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	/** 
	 * 조건에 맞는 멤버 조회DAO
	 * @author lee
	 * @param searching
	 * @return
	 */
	public List<Searching> checkSelect(Searching searching) {
		return sqlSession.selectList("searchingMapper.memberList", searching);
	}

	
	
	/** 인포 작성 유무 검사 DAO
	 * @param userNo
	 * @return result
	 */
	public int checkInfo(int userNo) {
		
		System.out.println("인포유무 DAO" + userNo);
		
		return sqlSession.selectOne("searchingMapper.checkInfo", userNo);
	}



	/** 내 인포 작성 DAO
	 * @param setInfo
	 * @return
	 */
	public int setInfo(Searching setInfo) {
		
		return sqlSession.insert("searchingMapper.setInfo", setInfo);
	}

}

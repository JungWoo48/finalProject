package edu.kh.fin.band.room.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.band.room.model.vo.Room;

@Repository
public class RoomDAO {

	@Autowired
	SqlSessionTemplate sqlSession;
	
	/** 연습실 조회
	 * @return
	 */
	public List<Room> roomList() {
		return sqlSession.selectList("pracRoom-mapper.roomList");
	}

	/** 연습실 내부 조회
	 * @param pracRoomNo
	 * @return
	 */
	public Room roomDetail(int pracRoomNo) {
		return sqlSession.selectOne("pracRoom-mapper.roomDetail",pracRoomNo);
	}

	/** 검색 기능
	 * @param region
	 * @return
	 */
	public List<Room> searchingRoomList(String region, String searchingText) {
		
		HashMap<String, Object> map = new HashMap<>(); 
		
		searchingText = '%' + searchingText + '%';
		region = '%' + region + '%';

		map.put("region", region);
		map.put("searchingText", searchingText);
		
		
		
		return sqlSession.selectList("pracRoom-mapper.searchingRoom",map);
	}

	public int pracRoomBooking(ArrayList<Integer> timeArr, HashMap<String, Object> map) {
		
		int rCount = 0;
		
		for(int i=0; i<timeArr.size(); i++) {
			
			map.put("thisTime", timeArr.get(i));
			
			sqlSession.insert("pracRoom-mapper.roomBook", map);
			
			rCount ++;
		}
		
		return rCount;
	}

	public List<Integer> checkBookingTime(HashMap<String, Object> dayMap) {
		return sqlSession.selectList("pracRoom-mapper.checkBookingTime", dayMap);
	}

	/**
	 * 예약을 할 때 오너에게 알람을 보내는 DAO
	 * @author lee
	 * @param map
	 * @return
	 */
	public int roomAlarmResult(HashMap<String, Object> map) {
		return sqlSession.insert("pracRoom-mapper.roomSetAlarm", map);
	}

	/**
	 * 예약 알람을 오너에게 보내기 위해서 오너 넘버를 가지고 오는 DAO
	 * @param map
	 * @return
	 */
	public int selectOwnerNo(HashMap<String, Object> map) {
		return sqlSession.selectOne("pracRoom-mapper.selectOwnerNo", map);
	}

}

import { Modal, Button, Image, Row, Col } from "antd";
import { useState } from "react";
import styled from "styled-components";
import { Link } from "react-router-dom";
import OGYM from '../../../assets/pages/mainPage/navButton/ogym.png';
import barChart from "../../../assets/pages/mainPage/navButton/bar-chart.png"
import conference from "../../../assets/pages/mainPage/navButton/conference.png"
import onlineBooking from "../../../assets/pages/mainPage/navButton/online-booking.png"
import reserved from "../../../assets/pages/mainPage/navButton/reserved.png"
import work from "../../../assets/pages/mainPage/navButton/work.png"

const StyledModal = styled(Modal)`
  position: fixed;
  top: 0px;
  left: 0px;
  bottom: 0px;
  height: "100vh";
  // animation: "none",
  // transition: "350ms",
  width: 100%;
  
  .ant-modal-wrap {
    height: 100%;
    width: 100%;
  }

  .ant-modal-content {
    height: 100%;
    width: 100vw;
    display: flex;
    flex-direction: column;
  }

  .ant-modal-body {
    display: flex;
    padding: 0;
    height: 100%
  }


`;
const StyledButton = styled(Button)`
    position: fixed;
    z-index: 2;
    top: 10px;
    left: 10px;
    width: 100px;
    height: 100px;
    object-fit: cover;
    // background-image: url(${OGYM});
`;



const MainNavigation = (): JSX.Element => {

    const [isVisible, setIsVisible] = useState<boolean>(false);
    
    const clickMenuButton = () => {
        setIsVisible(!isVisible)
    }

    const clickModalClose = () => {
        setIsVisible(!isVisible)
    }

    return (
        <>
            <StyledButton onClick={clickMenuButton}
          shape="circle"
          icon={<Image src={OGYM} preview={false} />}
        >
        
            </StyledButton>

            <StyledModal
                title="O-GYM"
                visible={isVisible}
                onOk={clickModalClose}
                onCancel={clickModalClose}
                transitionName="ant-move-up"
                maskTransitionName=""
                footer={null}
            >
              {/* 모달 내용 */}
              <Row style={{flex: 1, display: "flex", alignItems: "stretch"}}>
                <Col span={12} style={{backgroundColor: "#F08C8C", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column" }}>
                  <Link to={'/내건강분석'} style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", height: "50%", width: "15%" }}>
                  <img src={barChart} alt="내 건강분석" style={{width: "80%"}}/>
                  </Link>
                  <Link to={'/내건강분석'}>
                  <p style={{color: "white", fontSize: "1.5rem"}}>내 건강 분석</p>
                  </Link>
                </Col>
                <Col span={12} style={{backgroundColor: "#dcdcdc", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column"}}>
                <Link to={'/내학생관리'} style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", height: "50%", width: "15%" }}>
                <img src={work} alt="내 학생 관리" style={{width: "80%"}}/>
                </Link>
                <Link to={'/내학생관리'}>
                <p style={{color: "white", fontSize: "1.5rem", marginTop: "1rem"}}>내 학생 관리</p>
                </Link>
                </Col>
                <Col span={12} style={{backgroundColor: "#dcdcdc", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column"}}>
                <Link to={'/예약현황확인'} style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", height: "50%", width: "15%" }}>
                <img src={onlineBooking} alt="예약 현황 확인" style={{width: "80%"}}/>
                </Link>
                <Link to={'/예약현황확인'}>
                <p style={{color: "white", fontSize: "1.5rem", marginTop: "1rem"}}>예약 현황 확인</p>
                </Link>
                </Col>
                <Col span={12} style={{backgroundColor: "#96C7ED", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column" }}>
                <Link to={'/studentreservation'} style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", height: "50%", width: "15%" }}>
                  <img src={reserved} alt="예약" style={{width: "80%"}}/>
                </Link>
                <Link to={'/studentreservation'}>
                  <p style={{color: "white", fontSize: "1.5rem", marginTop: "1rem"}}>PT 예약 / 취소</p>
                </Link>
                </Col>
                <Col span={12} style={{backgroundColor: "#91F8D0", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column" }}>
                <Link to={'/화상채팅접속'} style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", height: "50%", width: "15%" }}>
                  <img src={conference} alt="예약" style={{width: "80%"}}/>
                </Link>
                <Link to={'/화상채팅접속'}>
                  <p style={{color: "white", fontSize: "1.5rem", marginTop: "1rem"}}>PT 화상 채팅방 접속하기</p>
                </Link>
                </Col>
                <Col span={12} style={{backgroundColor: "#dcdcdc", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column"}}>
                <Link to={'/화상채팅접속'} style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", height: "50%", width: "15%" }}>
                <img src={conference} alt="예약" style={{width: "80%"}}/>
                </Link>
                <Link to={'화상채팅접속'}>
                <p style={{color: "white", fontSize: "1.5rem", marginTop: "1rem"}}>PT 화상 채팅방 개설하기</p>
                </Link>
                </Col>
              </Row>
            </StyledModal>
            
        </>
    );
}

export default MainNavigation;
import { useEffect, useState } from "react";
import { Container, Table, Button } from "react-bootstrap";
import apiClient from "../../apiClient";
import { MANAGE_STANDARD_INFO_API } from "../../constants/API";

function MasterData() {
  const [writers, setWriters] = useState({
    "CASE 작성자": "",
    "CASE 작성부서": "",
    "DIP 작성자": "",
    "DIP 작성부서": "",
    "IM 작성자": "",
    "IM 작성부서": "",
    "메인생산일보 작성자": "",
    "메인생산일보 작성부서": "",
    "MANUAL 작성자": "",
    "MANUAL 작성부서": "",
    "PACKING 작성자": "",
    "PACKING 작성부서": "",
    "PCB 작성자": "",
    "PCB 작성부서": "",
    공수: 4.5,
    "SM 작성자": "",
    "SM 작성부서": "",
    "ACCY 작성자": "",
    "ACCY 작성부서": "",
    "구매자재 작성자": "",
    "구매자재 작성부서": "",
  });

  const [isEditMode, setIsEditMode] = useState(false); // 수정 모드 상태

  const handleInputChange = (e: any, field: string) => {
    setWriters((prevWriters) => ({
      ...prevWriters,
      [field]: e.target.value,
    }));
  };

  const handleEditClick = async () => {
    setIsEditMode(!isEditMode);
    if (isEditMode) {
      console.log(writers);
      await apiClient.put(MANAGE_STANDARD_INFO_API, writers);
    }
  };

  const handleData = async () => {
    const response = await apiClient.get(MANAGE_STANDARD_INFO_API);

    // 내려온 데이터를 영어 필드명으로 매핑
    const mappedData: any = {
      "CASE 작성자": response.data["CASE 작성자"],
      "CASE 작성부서": response.data["CASE 작성부서"],
      "DIP 작성자": response.data["DIP 작성자"],
      "DIP 작성부서": response.data["DIP 작성부서"],
      "IM 작성자": response.data["IM 작성자"],
      "IM 작성부서": response.data["IM 작성부서"],
      "메인생산일보 작성자": response.data["메인생산일보 작성자"],
      "메인생산일보 작성부서": response.data["메인생산일보 작성부서"],
      "MANUAL 작성자": response.data["MANUAL 작성자"],
      "MANUAL 작성부서": response.data["MANUAL 작성부서"],
      "PACKING 작성자": response.data["PACKING 작성자"],
      "PACKING 작성부서": response.data["PACKING 작성부서"],
      "PCB 작성자": response.data["PCB 작성자"],
      "PCB 작성부서": response.data["PCB 작성부서"],
      공수: response.data["공수"], // 숫자 데이터
      "SM 작성자": response.data["SM 작성자"],
      "SM 작성부서": response.data["SM 작성부서"],
      "LOSS 작성부서": response.data["LOSS 작성부서"],
      "LOSS 작성자": response.data["LOSS 작성자"],
      "ACCY 작성자": response.data["ACCY 작성자"],
      "ACCY 작성부서": response.data["ACCY 작성부서"],
      "구매자재 작성자": response.data["구매자재 작성자"],
      "구매자재 작성부서": response.data["구매자재 작성부서"],
    };

    setWriters(mappedData);
    console.log("초기 데이터: " + mappedData.caseDepartment);
  };

  useEffect(() => {
    handleData();
  }, []);

  return (
    <Container>
      <h3 className="mt-4">기준정보</h3>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>이름</th>
            <th>값</th>
          </tr>
        </thead>
        <tbody>
          {Object.keys(writers).map((key, index) => (
            <tr key={index}>
              <td>{index + 1}</td>
              <td>{key}</td>
              <td>
                {isEditMode ? (
                  <input
                    type="text"
                    value={(writers as any)[key]}
                    onChange={(e) => handleInputChange(e, key)}
                  />
                ) : (
                  (writers as any)[key]
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <div className="d-flex justify-content-end mt-3">
        <Button onClick={handleEditClick}>
          {isEditMode ? "제출" : "수정"}
        </Button>
      </div>
    </Container>
  );
}

export default MasterData;

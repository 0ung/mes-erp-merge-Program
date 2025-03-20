import { Col, Row } from "react-bootstrap";
import CustomButton from "./CustomButton";
import ReactToPrint from "react-to-print";

interface PrintPageType {
  handleExcelDownload?: () => void;
  handlePrintRef: React.RefObject<any>; // React ref로 인쇄할 컴포넌트 참조
}

function PrintPage({ handleExcelDownload, handlePrintRef }: PrintPageType) {
  return (
    <>
      <Row className="mt-4 justify-content-end no-print">
        <Col xs="auto">
          {handleExcelDownload && (
            <CustomButton onClick={handleExcelDownload}>
              엑셀 다운로드
            </CustomButton>
          )}
        </Col>
        <Col xs="auto" className="no-print">
          <ReactToPrint
            trigger={() => <CustomButton>출력</CustomButton>}
            content={() => handlePrintRef.current}
          />
        </Col>
      </Row>
    </>
  );
}

export default PrintPage;

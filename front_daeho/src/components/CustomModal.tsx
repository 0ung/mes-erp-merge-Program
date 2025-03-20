import React from "react";
import { Modal, Button } from "react-bootstrap";

interface CustomModalProps {
  show: boolean;
  handleClose: () => void;
  handleSave: () => void;
  children: React.ReactNode; // children을 제대로 선언
  title: string;
  activeModal?: boolean | (() => boolean);
}

const CustomModal: React.FC<CustomModalProps> = ({
  show,
  handleClose,
  handleSave,
  children,
  title,
  activeModal = false, // 잘못된 부분 수정
}) => {
  return (
    <Modal show={show} onHide={handleClose}>
      <Modal.Header closeButton>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>{children}</Modal.Body> {/* 전달받은 children을 렌더링 */}
      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          닫기
        </Button>
        {activeModal ? (
          <Button variant="primary" onClick={handleSave}>
            저장
          </Button>
        ) : (
          <Button disabled variant="primary" onClick={handleSave}>
            저장
          </Button>
        )}
      </Modal.Footer>
    </Modal>
  );
};

export default CustomModal;

import { ColDef, ColGroupDef, GridReadyEvent } from "ag-grid-community";
import { useState } from "react";
import { Button, Modal } from "react-bootstrap";
import styled from "styled-components";

// 필터 컨테이너 스타일 (더 넓게 설정)
const FilterContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin: 20px 0;
  max-width: 1000px; // 가로 사이즈를 더 넓게 설정
  padding: 20px;

  label {
    font-size: 14px;
    display: flex;
    align-items: center;
    background-color: #f1f3f5;
    padding: 8px;
    border-radius: 5px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s ease;
    cursor: pointer;
    &:hover {
      background-color: #e9ecef;
    }
  }

  input {
    margin-right: 10px;
  }
`;

// 모달 크기 조정을 위한 스타일
const StyledModal = styled(Modal)`
  .modal-dialog {
    max-width: 900px; // 모달 가로 크기 조정
  }

  .modal-content {
    padding: 20px;
  }
`;

// 컬럼 가시성 초기화 함수
const initializeVisibility = (
  columns: (ColDef | ColGroupDef)[]
): { [key: string]: boolean } => {
  const visibility: { [key: string]: boolean } = {};

  columns.forEach((col) => {
    if ("field" in col && col.field) {
      visibility[col.field] = !col.hide;
    }
    if ("children" in col && Array.isArray(col.children)) {
      Object.assign(visibility, initializeVisibility(col.children)); // 재귀적으로 모든 하위 컬럼을 찾음
    }
  });

  return visibility;
};

export const HideColumns = ({
  params,
  tableDef,
}: {
  params: GridReadyEvent;
  tableDef: (ColDef | ColGroupDef)[];
}) => {
  // 필드별로 상태 관리 - 재귀적으로 모든 계층의 컬럼 가시성 초기화
  const [columnVisibility, setColumnVisibility] = useState(() =>
    initializeVisibility(tableDef)
  );

  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const handleColumnToggle = (field: string) => {
    setColumnVisibility((prevState) => {
      const newVisibility = !prevState[field];
      params.api.setColumnsVisible([field], newVisibility);

      // tableDef에서 해당 컬럼의 hide 상태를 직접 수정
      tableDef.forEach((col) => {
        if ("field" in col && col.field === field) {
          col.hide = !newVisibility;
        }
        if ("children" in col && Array.isArray(col.children)) {
          updateChildColumnsVisibility(col.children, field, newVisibility);
        }
      });

      return {
        ...prevState,
        [field]: newVisibility,
      };
    });
  };

  // 하위 컬럼의 hide 상태도 업데이트하는 재귀 함수
  const updateChildColumnsVisibility = (
    columns: (ColDef | ColGroupDef)[],
    field: string,
    visible: boolean
  ) => {
    columns.forEach((col) => {
      if ("field" in col && col.field === field) {
        col.hide = !visible;
      }
      if ("children" in col && Array.isArray(col.children)) {
        updateChildColumnsVisibility(col.children, field, visible); // 하위 항목 재귀 처리
      }
    });
  };

  // 필터 렌더링 (모든 컬럼 개별 처리 및 그룹화 처리)
  const renderColumnFilters = (
    columns: (ColDef | ColGroupDef)[],
    parentHeaderName: string = ""
  ): JSX.Element[] => {
    return columns.flatMap((col) => {
      if ("children" in col && Array.isArray(col.children)) {
        const newParentHeaderName = parentHeaderName
          ? `${parentHeaderName} > ${col.headerName}`
          : col.headerName;

        const childFilters = renderColumnFilters(
          col.children,
          newParentHeaderName
        );

        // 그룹 헤더를 추가
        return [...childFilters];
      }

      if ("field" in col && col.field) {
        const headerLabel = parentHeaderName
          ? `${parentHeaderName} > ${col.headerName || col.field}`
          : col.headerName || col.field;

        return (
          <label key={col.field}>
            <input
              type="checkbox"
              checked={columnVisibility[col.field] ?? false}
              onChange={() => handleColumnToggle(col.field!)}
            />
            {headerLabel}
          </label>
        );
      }

      return [];
    });
  };

  return (
    <>
      <div className="no-print">
        <Button variant="primary" onClick={handleShow}>
          필터
        </Button>

        <StyledModal show={show} onHide={handleClose} centered>
          <Modal.Header closeButton>
            <Modal.Title>컬럼 필터</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <FilterContainer>{renderColumnFilters(tableDef)}</FilterContainer>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
              닫기
            </Button>
          </Modal.Footer>
        </StyledModal>
      </div>
    </>
  );
};

import styled from "styled-components";

const GridWrapper = styled.div<{ height: string }>`
  height: ${(props) => props.height}; /* height를 props로 설정 */

  /* 셀과 헤더 중앙 정렬 */
  .ag-cell,
  .ag-header-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 4px; /* 패딩을 줄여 빈 공간 최소화 */
    white-space: normal; /* 줄 바뀜을 허용 */
    word-break: break-word; /* 단어가 넘칠 경우 줄 바꿈 */
    font-size: 14px; /* 폰트 크기 설정 */
  }

  /* 헤더의 구분선 스타일 */
  .ag-header-cell,
  .ag-header-group-cell {
    border-right: 1px solid #e0e0e0; /* 헤더 셀에 구분선 추가 */
    padding: 0px 12px 0px 12px;
  }

  /* 마지막 헤더 셀의 구분선 제거 */
  .ag-header-cell:last-child,
  .ag-header-group-cell:last-child {
    border-right: none;
  }

  /* 데이터 셀 구분선 스타일 */
  .ag-cell {
    border-right: 1px solid #e0e0e0;
    border-bottom: 1px solid #e0e0e0;
  }

  /* 마지막 데이터 셀 구분선 제거 */
  .ag-cell:last-child {
    border-right: none;
  }

  /* 그룹 헤더의 텍스트 오버플로우 설정 */
  .ag-header-group-cell {
    text-overflow: clip;
  }

  /* 마지막 행의 하단 구분선 제거 */
  .ag-row:last-child .ag-cell {
    border-bottom: none;
  }
}
`;

function TableWrapper({
  children,
  className,
  height = "auto", // 기본값을 'auto'로 설정
}: {
  children: React.ReactNode;
  className?: string;
  height?: string; // height prop 추가
}) {
  return (
    <GridWrapper className={`${className} ag-theme-quartz`} height={height}>
      {children}
    </GridWrapper>
  );
}

export default TableWrapper;

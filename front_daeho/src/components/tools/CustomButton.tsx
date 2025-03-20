import React from "react";
import { Button, ButtonProps } from "react-bootstrap"; // ButtonProps로 버튼 타입 정의 가져오기
import styled from "styled-components";

// styled-components를 사용하여 버튼 스타일 정의
const StyledButton = styled(Button)<ButtonProps>`
  background-color: #19468e;
  border-color: #19468e;

  &:hover {
    background-color: #163b78;
    border-color: #163b78;
  }

  &:focus {
    background-color: #19468e;
    border-color: #163b78;
    box-shadow: 0 0 0 0.2rem rgba(25, 70, 142, 0.5);
  }
`;

interface CustomButtonProps extends ButtonProps {
  children: React.ReactNode; // children 속성 추가
  classNames?: string;
}

const CustomButton: React.FC<CustomButtonProps> = ({
  classNames,
  children,
  ...props
}) => {
  return (
    <StyledButton className={classNames} {...props}>
      {children}
    </StyledButton>
  );
};

export default CustomButton;

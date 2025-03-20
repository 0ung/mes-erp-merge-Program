import React, { CSSProperties } from "react";
import { Form, InputGroup } from "react-bootstrap";

// DateField Props 타입 정의
interface DateFieldProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  minDate?: string; // 추가된 minDate 속성
  style?: CSSProperties;
}

// DateField 컴포넌트
export const DateField: React.FC<DateFieldProps> = ({
  label,
  value,
  onChange,
  minDate,
  style, // className 기본값 추가
}) => (
  <InputGroup className={`d-flex align-items-center`}>
    {label && <Form.Label className="fw-bold mt-2 me-2">{label}</Form.Label>}
    <Form.Control
      type="date"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      min={minDate} // 최소 날짜 설정
      className="form-control-sm" // 필드 크기 조정
      style={style}
    />
  </InputGroup>
);

// TextField Props 타입 정의
interface TextFieldProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  style?: CSSProperties;
}

// TextField 컴포넌트
export const TextField: React.FC<TextFieldProps> = ({
  label,
  value,
  onChange,
  style,
}) => (
  <InputGroup className={`d-flex align-items-center`}>
    {label && <Form.Label className="fw-bold mt-2 me-2">{label}</Form.Label>}
    <Form.Control
      type="text"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="form-control-sm" // 필드 크기 조정
      style={style}
    />
  </InputGroup>
);

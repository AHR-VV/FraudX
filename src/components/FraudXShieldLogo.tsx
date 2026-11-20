import React from 'react';

interface FraudXShieldLogoProps {
  className?: string;
}

export const FraudXShieldLogo: React.FC<FraudXShieldLogoProps> = ({ className = 'w-6 h-7' }) => (
  <svg
    viewBox="0 0 28 32"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={className}
    aria-hidden="true"
  >
    <path
      d="M14 1.5L25.5 6.2V14.8C25.5 22.4 20.5 27.9 14 30.5C7.5 27.9 2.5 22.4 2.5 14.8V6.2L14 1.5Z"
      fill="#94AEB8"
    />
    <path
      d="M14 3.8L4.7 7.6V14.7C4.7 21.0 8.8 25.7 14 28.1V3.8Z"
      fill="#CFE1E8"
    />
    <path
      d="M14 3.8L23.3 7.6V14.7C23.3 21.0 19.2 25.7 14 28.1V3.8Z"
      fill="#7B9BA8"
    />
  </svg>
);

import { writable } from "svelte/store";

const codeColorCnt = 10;
export const codeColor = writable(Array(codeColorCnt).fill());
export const isPaused = writable(true); 
export const animationWorking = writable(false);
export const dummyAnimationWorking = writable(false); // 가짜 애니메이션실행 전역 변수 => 연속으로 애니메이션 실행할 경우 사용
export const pausedIcon = writable(true);
export const fromBtn = writable(false);
export const isReplay = writable(false);
export const explanation = writable(``);
export const animationSpeed = writable(1);
export const animationQuery = writable([]);
export const animationStep = writable([0, 0]); // [curStep, maxStep]
export const asyncCnt = writable(0); // 비동기 함수 한 번에 하나만 실행하기 위한 변수
export const gradient = writable(0);
export const indentSize = writable(35);
export const maxSpeed = writable(500);
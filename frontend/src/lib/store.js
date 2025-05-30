import { writable } from 'svelte/store';
import { OK } from "./httpStatusStore.js";

export let isListVisible = writable(false); // 알고리즘 리스트의 가시성을 관리하는 변수
export let isLoginVisible = writable(false); // 로그인 팝업의 가시성을 관리하는 변수
export let isLogin = writable(false); // 로그인 유무를 관리하는 변수
export let isAlgoGuideVisible = writable(false); // 알고리즘 가이드 팝업의 가시성을 관리하는 변수
export let isTeamsVisible = writable(false); // Footer Teams의 가시성을 관리하는 변수
export let isAboutVisible = writable(false); // Footer About의 가시성을 관리하는 변수
export const algoGuideDelay = writable(350); // 알고리즘 가이드 팝업의 flip 애니메이션 시간을 관리하는 변수

export const checkLoginStatus = async () => {
    const response = await fetch('/auth/status', { credentials: 'include' });

    if (response.status === OK) {
      isLogin.set(true);
    }
    else {
      isLogin.set(false);
    }
};
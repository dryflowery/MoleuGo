import { writable } from 'svelte/store';

export let isListVisible = writable(false); // 알고리즘 리스트의 가시성을 관리하는 변수
export let isLoginVisible = writable(false); // 로그인 팝업의 가시성을 관리하는 변
export let isLogin = writable(false)

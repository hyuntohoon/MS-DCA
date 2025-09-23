# ☁️ 클라우드 미니프로젝트

## 📊 K6 성능 테스트 대시보드
Docker / Kubernetes 환경에서 **K6 부하 테스트**를 실행하고, 결과를 시각화 대시보드로 확인할 수 있는 프로젝트입니다.

---

### 🚀 주요 기능
- K6 성능 테스트 실행 (Docker, Kubernetes 비교)
- 처리량 / 에러율 / 응답시간 그래프 시각화
- 상태 아이콘 표시 (✅ 정상 / ⚠️ 주의 / ❌ 위험)

---

### 🛠️ 기술 스택
- **Frontend**: Vue 3, Vite, Chart.js, Axios  
- **Backend**: Spring Boot, MongoDB  
- **Infra**: Docker, Kubernetes, K6  

---

### ✅ 성능 지표 기준
- ❌ 위험 : 에러율 ≥ 10% 또는 응답시간 ≥ 5초  
- ⚠️ 주의 : 에러율 ≥ 5% 또는 응답시간 ≥ 2초  
- ✅ 정상 : 그 외의 경우  

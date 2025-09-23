<template>
  <div>
    <!-- 헤더 -->
    <div class="header">
      <h2 class="main-title">📊 K6 성능 테스트 결과 (최근 5개)</h2>
    </div>

    <!-- 로딩 오버레이 -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>성능 테스트 실행 중... 잠시만 기다려주세요</p>
    </div>

    <!-- 토스트 알림 -->
    <div v-if="toastMessage" class="toast" :class="{ show: showToast }">
      {{ toastMessage }}
    </div>

    <!-- 📊 차트 -->
    <div class="chart-container">
      <h3>처리량 (건)</h3>
      <BarChart v-if="requestChartData" :data="requestChartData" :options="defaultChartOptions" />
    </div>

    <div class="chart-container">
      <h3>에러율 (%)</h3>
      <BarChart v-if="errorChartData" :data="errorChartData" :options="errorChartOptions" />
    </div>

    <div class="chart-container">
      <h3>평균 응답시간 (초)</h3>
      <BarChart
        v-if="responseTimeChartData"
        :data="responseTimeChartData"
        :options="responseTimeChartOptions"
      />
    </div>

    <!-- 📋 사이드 실행 리스트 -->
    <div class="side-results">
      <button class="test-btn" @click="runK6Test" :disabled="loading">
        <span v-if="loading">⏳ 실행 중...</span>
        <span v-else>테스트 실행</span>
      </button>
      <div class="result-container">
        <h4>🐳 Docker</h4>
        <ul>
          <li v-for="(item, idx) in [...dockerList].reverse()" :key="idx">
            {{ idx + 1 }}. 처리량: {{ item.requestCount }} / 에러율:
            {{ (item.errorRate * 100).toFixed(1) }}% / 응답: {{ item.avgResponseTime.toFixed(2) }}초
            <span>{{ getStatusIcon(item) }}</span>
          </li>
        </ul>
      </div>

      <div class="result-container">
        <h4>🚀 Kubernetes</h4>
        <ul>
          <li v-for="(item, idx) in [...kubeList].reverse()" :key="idx">
            {{ idx + 1 }}. 처리량: {{ item.requestCount }} / 에러율:
            {{ (item.errorRate * 100).toFixed(1) }}% / 응답: {{ item.avgResponseTime.toFixed(2) }}초
            <span>{{ getStatusIcon(item) }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue'
import axios from 'axios'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js'
import { Bar } from 'vue-chartjs'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default defineComponent({
  name: 'K6Chart',
  components: { BarChart: Bar },
  setup() {
    const requestChartData = ref(null)
    const errorChartData = ref(null)
    const responseTimeChartData = ref(null)
    const loading = ref(false)

    // 리스트용
    const dockerList = ref([])
    const kubeList = ref([])

    // 토스트 알림
    const toastMessage = ref('')
    const showToast = ref(false)

    // 📊 처리량
    const defaultChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      layout: {
        padding: {
          bottom: 30, // ⬅️ 하단 여유 공간 확보
        },
      },
      scales: {
        x: {
          ticks: {
            color: '#333',
            padding: 10,
          },
        },
        y: {
          beginAtZero: true,
        },
      },
    }

    // 📊 에러율 (고정 0~100)
    const errorChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      layout: {
        padding: {
          bottom: 30,
        },
      },
      scales: {
        x: {
          ticks: {
            color: '#333',
            padding: 10,
          },
        },
        y: {
          beginAtZero: true,
          min: 0,
          max: 100,
        },
      },
    }

    // 📊 응답시간 (자동, 최소 10초 보장)
    const responseTimeChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      layout: {
        padding: {
          bottom: 30,
        },
      },
      scales: {
        x: {
          ticks: {
            color: '#333',
            padding: 10,
          },
        },
        y: {
          beginAtZero: true,
          suggestedMin: 0, // 최소값은 0부터
          suggestedMax: 10, // 최소 10초 보장
        },
      },
    }

    const fetchData = async () => {
      try {
        const response = await axios.get('http://localhost/backend/api/k6/results')
        const allData = response.data.data

        const docker = allData
          .filter((i) => i.category === 'docker')
          .sort((a, b) => new Date(a.executedAt) - new Date(b.executedAt))
          .slice(-5)
        const kube = allData
          .filter((i) => i.category === 'kubernetes')
          .sort((a, b) => new Date(a.executedAt) - new Date(b.executedAt))
          .slice(-5)

        dockerList.value = docker
        kubeList.value = kube

        // 라벨 포맷 변경
        const labels = docker.map((i) => {
          const date = new Date(i.executedAt)
          const h = String(date.getHours()).padStart(2, '0')
          const m = String(date.getMinutes()).padStart(2, '0')
          const s = String(date.getSeconds()).padStart(2, '0')
          return `${h}시 ${m}분 ${s}초`
        })

        // 📊 처리량
        requestChartData.value = {
          labels,
          datasets: [
            {
              label: 'Docker',
              backgroundColor: 'rgba(100, 181, 246, 0.7)', // 💙 파스텔 블루
              borderColor: 'rgba(100, 181, 246, 1)',
              borderWidth: 2,
              data: docker.map((i) => i.requestCount),
            },
            {
              label: 'Kubernetes',
              backgroundColor: 'rgba(244, 143, 177, 0.7)', // 💖 파스텔 핑크
              borderColor: 'rgba(244, 143, 177, 1)',
              borderWidth: 2,
              data: kube.map((i) => i.requestCount),
            },
          ],
        }

        // 📊 에러율
        errorChartData.value = {
          labels,
          datasets: [
            {
              label: 'Docker',
              backgroundColor: 'rgba(100, 181, 246, 0.7)',
              borderColor: 'rgba(100, 181, 246, 1)',
              borderWidth: 2,
              data: docker.map((i) => (i.errorRate * 100).toFixed(2)),
            },
            {
              label: 'Kubernetes',
              backgroundColor: 'rgba(244, 143, 177, 0.7)',
              borderColor: 'rgba(244, 143, 177, 1)',
              borderWidth: 2,
              data: kube.map((i) => (i.errorRate * 100).toFixed(2)),
            },
          ],
        }

        // 📊 응답시간
        responseTimeChartData.value = {
          labels,
          datasets: [
            {
              label: 'Docker',
              backgroundColor: 'rgba(100, 181, 246, 0.7)',
              borderColor: 'rgba(100, 181, 246, 1)',
              borderWidth: 2,
              data: docker.map((i) => i.avgResponseTime.toFixed(2)),
            },
            {
              label: 'Kubernetes',
              backgroundColor: 'rgba(244, 143, 177, 0.7)',
              borderColor: 'rgba(244, 143, 177, 1)',
              borderWidth: 2,
              data: kube.map((i) => i.avgResponseTime.toFixed(2)),
            },
          ],
        }
      } catch (err) {
        console.error('데이터 불러오기 실패:', err)
      }
    }

    const showToastMessage = (msg) => {
      toastMessage.value = msg
      showToast.value = true
      setTimeout(() => {
        showToast.value = false
      }, 3000)
    }

    const runK6Test = async () => {
      try {
        loading.value = true
        await axios.post('http://localhost/backend/api/k6')
        await fetchData()
        showToastMessage('✅ 성능 테스트 완료!')
      } catch (err) {
        showToastMessage('❌ 테스트 실행 실패')
        console.error(err)
      } finally {
        loading.value = false
      }
    }

    // 상태 아이콘 함수(기준)
    const getStatusIcon = (item) => {
      const errorRate = item.errorRate * 100
      const resp = item.avgResponseTime

      if (errorRate >= 10 || resp >= 5) return '❌' // 빨강
      if (errorRate >= 5 || resp >= 2) return '⚠️' // 노랑
      return '✅' // 초록
    }

    onMounted(fetchData)

    return {
      requestChartData,
      errorChartData,
      responseTimeChartData,
      defaultChartOptions,
      errorChartOptions,
      responseTimeChartOptions,
      runK6Test,
      loading,
      toastMessage,
      showToast,
      dockerList,
      kubeList,
      getStatusIcon,
    }
  },
})
</script>

<style>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 30px 40px;
}

.main-title {
  color: white;
  font-size: 30px;
  font-weight: bold;
  text-align: center;
  width: 100%;
}

.test-btn {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}
.test-btn:disabled {
  background-color: #9e9e9e;
  cursor: not-allowed;
}
.test-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 18px;
  z-index: 1000;
}
.spinner {
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: #323232;
  color: #fff;
  padding: 12px 20px;
  border-radius: 6px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  font-size: 14px;
  z-index: 2000;
  opacity: 0;
  transition:
    opacity 0.5s ease,
    top 0.5s ease;
}
.toast.show {
  opacity: 1;
  top: 40px;
}

.chart-container {
  width: 1000px;
  height: 500px;
  margin: 40px auto;
  padding: 20px;
  background: #fafafa;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.chart-container h3 {
  margin-bottom: 20px;
  text-align: center;
  color: #000;
  font-weight: bold;
}

/* 📋 사이드 리스트 */
.side-results {
  position: fixed;
  top: 120px;
  right: 20px;
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.result-container {
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  font-size: 14px;
}
.result-container h4 {
  margin-bottom: 8px;
  font-weight: bold;
  color: #000;
}
.result-container ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.result-container li {
  margin: 4px 0;
  color: #333;
}
</style>

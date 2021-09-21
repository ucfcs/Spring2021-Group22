async function renderChatCountColumnChart(element) {
  const data = await getData('chat_count_column_chart.json')
  
  const options = {
    title: {
      text: 'Chat Messages Usage',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Count',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}

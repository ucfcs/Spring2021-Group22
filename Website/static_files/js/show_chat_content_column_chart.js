async function renderChatContentColumnChart(element) {
  const data = await getData('chat_content_column_chart.json')
  
  const options = {
    title: {
      text: 'Chat Content Usage',
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

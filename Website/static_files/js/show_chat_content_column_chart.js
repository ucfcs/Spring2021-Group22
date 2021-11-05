async function renderChatContentColumnChart(element) {
  const data = await getData('chat_content_column_chart.json')
  
  const options = {
    title: {
      text: 'Total Chat Content By Player',
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
        text: 'Content Length',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}

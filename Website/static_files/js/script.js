;(async () => {
    {
      const raw = await fetch('/data/sharing_column_chart.json')
      const data = await raw.json()

      const options = {
        title: {
          text: 'Items Shared Among Us',
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

      new ApexCharts(document.querySelector('#sharing_is_caring'), options).render();
    }
    {
      const raw = await fetch('/data/locations_total_time_column_chart.json')
      const data = await raw.json()

      const options = {
        title: {
          text: 'Approximate Seconds Spent in Locations by Player',
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
            text: 'Seconds',
          },
        },
      }
      new ApexCharts(document.querySelector('#location_time'), options).render();
    }
    {
      const raw = await fetch('/data/locations_total_time_column_chart_v2.json')
      const data = await raw.json()

      const options = {
        title: {
          text: 'Approximate Seconds Spent in Locations by Player V2',
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
            text: 'Seconds',
          },
        },
      }
      new ApexCharts(document.querySelector('#location_time_v2'), options).render();
    }
  })()
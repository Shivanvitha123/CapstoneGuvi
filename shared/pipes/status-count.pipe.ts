import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusCount',
  standalone: true
})
export class StatusCountPipe implements PipeTransform {

  transform(
    policies: { status: string }[],
    status: string
  ): number {

    return policies.filter(
      policy => policy.status === status
    ).length;
  }
}
